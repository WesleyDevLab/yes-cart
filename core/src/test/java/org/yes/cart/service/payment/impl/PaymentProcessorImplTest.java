package org.yes.cart.service.payment.impl;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ShoppingCart;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.impl.TestPaymentGatewayImpl;
import org.yes.cart.payment.persistence.entity.impl.PaymentGatewayParameterEntity;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;
import org.yes.cart.shoppingcart.impl.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentProcessorImplTest extends BaseCoreDBTestCase {

    private final static String PGLABEL = "testPaymentGatewayLabel";


    private PaymentProcessorFactory paymentProcessorFactory;
    private CustomerOrderService customerOrderService;
    private CustomerOrderPaymentService customerOrderPaymentService;
    private AddressService addressService = null;
    private CustomerService customerService = null;
    private ShopService shopService = null;


    public void setUp() throws Exception {

        super.setUp();
        paymentProcessorFactory = (PaymentProcessorFactory) ctx.getBean(ServiceSpringKeys.PAYMENT_PROCESSOR_FACTORY);
        customerOrderService = (CustomerOrderService) ctx.getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        customerOrderPaymentService = (CustomerOrderPaymentService) ctx.getBean(ServiceSpringKeys.ORDER_PAYMENT_SERICE);
        addressService = (AddressService) ctx.getBean(ServiceSpringKeys.ADDRESS_SERVICE);
        customerService = (CustomerService) ctx.getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        shopService = (ShopService) ctx.getBean(ServiceSpringKeys.SHOP_SERVICE);

        createCustomer();

    }

    public void tearDown() {
        paymentProcessorFactory = null;
        customerOrderService = null;
        customerOrderPaymentService = null;
        addressService = null;
        customerService = null;
        shopService = null;
        super.tearDown();
    }

    /**
     * Test, to prove, that one order with one devivery produce one AUTH operation
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthorize1() throws Exception {

        final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        final CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart1(ctx), true);
        final Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        final CustomerOrderDelivery delivery0 = iter.next();

        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));

        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(),
                delivery0.getDevileryNum(),
                Payment.PAYMENT_STATUS_OK,
                PaymentGateway.AUTH).size());


    }

    /**
     * Test, to prove, that one order with one devivery produce one AUTH operation
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthCapture1() throws Exception {


        final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        final CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart1(ctx), true);
        final Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        final CustomerOrderDelivery delivery0 = iter.next();

        try {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorize(false);

            assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));

            assertEquals(1, customerOrderPaymentService.findBy(
                    customerOrder.getOrdernum(),
                    null,
                    Payment.PAYMENT_STATUS_OK,
                    PaymentGateway.AUTH_CAPTURE).size());
        } finally {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorize(true);
        }
    }

    /**
     * Test, to prove, that one order with two shipments produce two AUTH operation, in case
     * of no errors on payment gateways
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthorize2() throws Exception {

        final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        final CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(ctx), false);
        final Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        final CustomerOrderDelivery delivery0 = iter.next();
        final CustomerOrderDelivery delivery1 = iter.next();

        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, null).size());

        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDevileryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDevileryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());

    }

    /**
     * Test, to prove, that one order with two shipments produce two AUTH_CAPTURE operation, in case
     * of no errors on payment gateways
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthCapture2() throws Exception {

        final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        final CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(ctx), false);
        final Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();

        try {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorize(false);

            assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
            assertEquals(2, customerOrderPaymentService.findBy(
                    customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH_CAPTURE).size());


        } finally {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorize(true);

        }


    }

    /**
     * Test, to prove, that one order with two shipments produce one AUTH operation with failed status
     * in case of erros on payment gateway.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthorize3() throws Exception {

        try {
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL, new PaymentGatewayParameterEntity());

            final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
            final CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(ctx), false);
            final Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
            final CustomerOrderDelivery delivery0 = iter.next();


            assertEquals(Payment.PAYMENT_STATUS_FAILED, paymentProcessor.authorize(customerOrder, createParametersMap()));
            assertEquals(
                    "No payment for second delivery, because first is failed",
                    1,
                    customerOrderPaymentService.findBy(
                            customerOrder.getOrdernum(),
                            delivery0.getDevileryNum(),
                            Payment.PAYMENT_STATUS_FAILED,
                            PaymentGateway.AUTH).size()
            );

            assertEquals(
                    "No reverse auth operation, because nothing to reverse",
                    1,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null).size());

        } catch (Exception e) {
            assertTrue(e.getMessage(), false);

        } finally {
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL, null);

        }


    }

    /**
     * One order , two shipment. Payment gateway failed second auth operation ,
     * hence three payment records must be present:
     * first auth with ok status;
     * second with failed status;
     * first reverse auth;
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthorize4() throws Exception {
        try {

            TestPaymentGatewayImpl.setAuthNum(0);
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL_NO + "1", new PaymentGatewayParameterEntity());

            final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
            final CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(ctx), false);

            assertEquals(Payment.PAYMENT_STATUS_FAILED, paymentProcessor.authorize(customerOrder, createParametersMap()));
            assertEquals(
                    "Two Auth with different statuses and reverse auth must be present",
                    3,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null).size());

            assertEquals(
                    "One auth with ok status",
                    1,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());

            assertEquals(
                    "One auth with failed status",
                    1,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_FAILED, PaymentGateway.AUTH).size());

            assertEquals(
                    "One reverse auth with ok status",
                    1,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, PaymentGateway.REVERSE_AUTH).size());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        } finally {
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL, null);
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL_NO + "1", null);
        }
    }


    /**
     * One order , two shipment. Payment gateway throws exception on second auth operation,
     * hence three payment records must be present:
     * first auth with ok status;
     * second with failed status;
     * first reverse auth;
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthorize5() throws Exception {
        try {

            TestPaymentGatewayImpl.setAuthNum(0);
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_EXCEPTION_NO + "1", new PaymentGatewayParameterEntity());

            final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
            final CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(ctx), false);

            assertEquals(Payment.PAYMENT_STATUS_FAILED, paymentProcessor.authorize(customerOrder, createParametersMap()));
            assertEquals(
                    "Two Auth with different statuses and reverse auth must be present",
                    3,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null).size());

            assertEquals(
                    "One auth with ok status",
                    1,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());

            assertEquals(
                    "One auth with failed status",
                    1,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_FAILED, PaymentGateway.AUTH).size());

            assertEquals(
                    "One reverse auth with ok status",
                    1,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, PaymentGateway.REVERSE_AUTH).size());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        } finally {
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL, null);
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_EXCEPTION_NO + "1", null);
        }
    }


    /**
     * Test, to prove, that one order with two shipments produce one AUTH operation, in case if payment gateway
     * not supports multiple payments for one order.
     * Amount of payments must be equals for two cases, supports and not supports.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthorize6() throws Exception {

        BigDecimal amountForOnePayment = BigDecimal.ZERO;
        BigDecimal amountForTwoPayments;

        final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        final CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(ctx), false); //multiple delivery
        final Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        final CustomerOrderDelivery delivery0 = iter.next();

        final CustomerOrder customerOrder2 = customerOrderService.createFromCart(getShoppingCart2(ctx), false); //multiple delivery
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder2, createParametersMap()));
        amountForTwoPayments = customerOrderPaymentService.findBy(
                customerOrder2.getOrdernum(), null, null, null).get(0).getPaymentAmount();
        amountForTwoPayments = amountForTwoPayments.add(customerOrderPaymentService.findBy(
                customerOrder2.getOrdernum(), null, null, null).get(1).getPaymentAmount());


        try {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorizePerShipment(false);

            assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
            assertEquals(1, customerOrderPaymentService.findBy(
                    customerOrder.getOrdernum(), null, null, null).size());

            assertEquals(1, customerOrderPaymentService.findBy(
                    customerOrder.getOrdernum(),
                    customerOrder.getOrdernum(), // in single pay delivery num eq order num from payment point of view
                    Payment.PAYMENT_STATUS_OK,
                    PaymentGateway.AUTH).size());

            amountForOnePayment = customerOrderPaymentService.findBy(
                    customerOrder.getOrdernum(), null, null, null).get(0).getPaymentAmount();

        } catch (Exception ex) {
            assertTrue(ex.getMessage(), false);
        } finally {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorizePerShipment(true);
        }

        assertEquals(amountForOnePayment, amountForTwoPayments);
    }


    /**
     * Test , that perform fund capture when  shipment is completed.
     * Test case for single delivery and single payments.
     * Fund will be captured on delivery
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testShipmentComplete1() throws Exception {

        final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        final CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart1(ctx), false); //multiple delivery enabled
        final Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        final CustomerOrderDelivery delivery0 = iter.next();

        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), null, null, null).size());

        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(),
                delivery0.getDevileryNum(),
                Payment.PAYMENT_STATUS_OK,
                PaymentGateway.AUTH).size());

        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery0.getDevileryNum()));

        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(),
                delivery0.getDevileryNum(),
                Payment.PAYMENT_STATUS_OK,
                PaymentGateway.CAPTURE).size());

        //total two records auth and capture
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(),
                delivery0.getDevileryNum(),
                Payment.PAYMENT_STATUS_OK,
                null).size());


    }

    /**
     * Test , that perform fund capture when  shipment is completed.
     * Test case for single delivery and single payments.
     * Fund will be captured on delivery
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testShipmentComplete2() throws Exception {

        final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        final CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(ctx), false);
        final Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        final CustomerOrderDelivery delivery0 = iter.next();
        final CustomerOrderDelivery delivery1 = iter.next();

        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, null).size());

        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDevileryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDevileryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());

        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery1.getDevileryNum()));

        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDevileryNum(), Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDevileryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.CAPTURE).size());

        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery0.getDevileryNum()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDevileryNum(), Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDevileryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.CAPTURE).size());

        assertEquals(4, customerOrderPaymentService.findBy( //two auth and two captures
                customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, null).size());

        assertEquals(2, customerOrderPaymentService.findBy( //two captures
                customerOrder.getOrdernum(), null, null, PaymentGateway.CAPTURE).size());


    }


    /**
     * Test , to prove , that fund will captured on second delivery
     * when it failed on first
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testShipmentComplete3() throws Exception {

        final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        final CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(ctx), false);
        final Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        final CustomerOrderDelivery delivery0 = iter.next();
        final CustomerOrderDelivery delivery1 = iter.next();

        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, null).size());

        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDevileryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDevileryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());


        TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.CAPTURE_FAIL, new PaymentGatewayParameterEntity());
        assertEquals(Payment.PAYMENT_STATUS_FAILED, paymentProcessor.shipmentComplete(customerOrder, delivery1.getDevileryNum()));
        TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.CAPTURE_FAIL, null);

        assertEquals(2, customerOrderPaymentService.findBy(customerOrder.getOrdernum(), delivery1.getDevileryNum(), null, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDevileryNum(), Payment.PAYMENT_STATUS_FAILED, PaymentGateway.CAPTURE).size());

        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery0.getDevileryNum()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDevileryNum(), Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDevileryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.CAPTURE).size());

        assertEquals(3, customerOrderPaymentService.findBy( //two auth and one capture
                customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(4, customerOrderPaymentService.findBy( //two auth and two capture
                customerOrder.getOrdernum(), null, null, null).size());



    }


    /**
     * Test, to prove, that shipment complete not impact auth_capture records
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testShipmentComplete4() throws Exception {

        final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        final CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(ctx), false);
        final Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        final CustomerOrderDelivery delivery0 = iter.next();
        final CustomerOrderDelivery delivery1 = iter.next();


        try {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorize(false);

            assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
            assertEquals(2, customerOrderPaymentService.findBy(
                    customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH_CAPTURE).size());

            assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery1.getDevileryNum()));
            assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery0.getDevileryNum()));
            assertEquals(2, customerOrderPaymentService.findBy(
                    customerOrder.getOrdernum(), null, null, null).size());


        } finally {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorize(true);

        }


    }


    @Test
    public void testCancelOrder1() throws Exception {


        final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        final CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(ctx), false);
        final Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        final CustomerOrderDelivery delivery0 = iter.next();
        final CustomerOrderDelivery delivery1 = iter.next();

        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, null).size());

        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDevileryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDevileryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());

        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery1.getDevileryNum()));

        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDevileryNum(), Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDevileryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.CAPTURE).size());

        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery0.getDevileryNum()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDevileryNum(), Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDevileryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.CAPTURE).size());

        assertEquals(4, customerOrderPaymentService.findBy( //two auth and two captures
                customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, null).size());

        assertEquals(2, customerOrderPaymentService.findBy( //two captures
                customerOrder.getOrdernum(), null, null, PaymentGateway.CAPTURE).size());


        //cancel order
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.cancelOrder(customerOrder));


        //
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDevileryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.VOID_CAPTURE).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDevileryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.VOID_CAPTURE).size());


        //TODO test with cancel state wait till customerOrder.setOrderStatus();




    }


    private void createCustomer() {

        Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);
        customer.setEmail("jd@domain.com");
        customer.setFirstname("John");
        customer.setLastname("Dou");
        customer = customerService.create(customer, shopService.getById(10L));
        assertTrue(customer.getCustomerId() > 0);

        Address address = addressService.getGenericDao().getEntityFactory().getByIface(Address.class);
        address.setFirstname("John");
        address.setLastname("Dou");
        address.setCity("LA");
        address.setAddrline1("line1");
        address.setCountryCode("US");
        address.setAddressType(Address.ADDR_TYPE_SHIPING);
        address.setCustomer(customer);

        addressService.create(address);


        address = addressService.getGenericDao().getEntityFactory().getByIface(Address.class);
        address.setFirstname("John");
        address.setLastname("Dou");
        address.setCity("New-Vasyki");
        address.setAddrline1("line0");
        address.setCountryCode("ZH");
        address.setAddressType(Address.ADDR_TYPE_BILLING);
        address.setCustomer(customer);

        addressService.create(address);
    }


    private Map<String, String> createParametersMap() {
        Map<String, String> rez = new HashMap<String, String>();
        rez.put("ccHolderName", "John Dou");
        rez.put("ccNumber", "4111111111111111");
        rez.put("ccExpireMonth", "12");
        rez.put("ccExpireYear", "2020");
        rez.put("ccSecCode", "111");
        rez.put("ccType", "Visa");
        return rez;
    }


    /**
     * Create simple cart with products, that have a standard availibility and enough qty on warehouses.
     *
     * @param context app context
     * @return cart
     */
    private ShoppingCart getShoppingCart1(final ApplicationContext context) {


        ShoppingCart shoppingCart = getEmptyCart(context);

        new AddSkuToCartEventCommandImpl(context, Collections.singletonMap(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST1"))
                .execute(shoppingCart);

        new AddSkuToCartEventCommandImpl(context, Collections.singletonMap(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST3"))
                .execute(shoppingCart);

        new AddSkuToCartEventCommandImpl(context, Collections.singletonMap(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST3"))
                .execute(shoppingCart);

        return shoppingCart;
    }

    /**
     * Bot sku with standard availability , but one of the has not qty on warehouse
     *
     * @param context app context
     * @return cart
     */
    private ShoppingCart getShoppingCart2(final ApplicationContext context) {

        ShoppingCart shoppingCart = getEmptyCart(context);

        new AddSkuToCartEventCommandImpl(context, Collections.singletonMap(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST4"))
                .execute(shoppingCart);

        new AddSkuToCartEventCommandImpl(context, Collections.singletonMap(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST4-M"))
                .execute(shoppingCart);

        return shoppingCart;
    }

    private ShoppingCart getEmptyCart(final ApplicationContext context) {
        ShoppingCart shoppingCart = new ShoppingCartImpl();

        Map<String, String> params = new HashMap<String, String>();
        params.put(LoginCommandImpl.EMAIL, "jd@domain.com");
        params.put(LoginCommandImpl.NAME, "John Doe");


        shoppingCart.setShopId(10);

        new ChangeCurrencyEventCommandImpl(context, Collections.singletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, "USD"))
                .execute(shoppingCart);

        new LoginCommandImpl(null, params)
                .execute(shoppingCart);

        new SetCarrierSlaCartCommandImpl(null, Collections.singletonMap(SetCarrierSlaCartCommandImpl.CMD_KEY, "1"))
                .execute(shoppingCart);

        return shoppingCart;
    }


}