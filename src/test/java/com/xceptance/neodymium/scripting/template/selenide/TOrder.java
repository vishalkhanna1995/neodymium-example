/**
 * 
 */
package com.xceptance.neodymium.scripting.template.selenide;

import org.junit.Test;

import com.xceptance.multibrowser.TestTargets;
import com.xceptance.neodymium.scripting.template.selenide.flow.FOpenHomepage;
import com.xceptance.neodymium.scripting.template.selenide.objects.Address;
import com.xceptance.neodymium.scripting.template.selenide.objects.CreditCard;
import com.xceptance.neodymium.scripting.template.selenide.objects.Product;
import com.xceptance.neodymium.scripting.template.selenide.page.PCategory;
import com.xceptance.neodymium.scripting.template.selenide.page.PHome;
import com.xceptance.neodymium.scripting.template.selenide.page.PProduct;
import com.xceptance.neodymium.scripting.template.selenide.page.checkout.PCart;
import com.xceptance.neodymium.scripting.template.selenide.page.checkout.PNewBillingAddress;
import com.xceptance.neodymium.scripting.template.selenide.page.checkout.PNewShippingAddress;
import com.xceptance.neodymium.scripting.template.selenide.page.checkout.PPayment;
import com.xceptance.neodymium.scripting.template.selenide.page.checkout.PPlaceOrder;
import com.xceptance.neodymium.scripting.template.selenide.page.user.PLogin;

/**
 * @author pfotenhauer
 */
@TestTargets(
{
  "Chrome_1024x768"
})
public class TOrder extends BasicTest
{
    @Test
    public void test()
    {
        // Page types to use
        PHome homePage;
        PLogin loginPage;
        PCategory categoryPage;
        PProduct productPage;
        PCart cartPage;
        PNewShippingAddress shippingAddressPage;
        PNewBillingAddress billingAddressPage;
        PPayment paymentPage;
        PPlaceOrder placeOrderPage;

        // total product count will be updated throughout the test
        int totalCount = 0;

        // Goto homepage
        homePage = new FOpenHomepage().flow();
        homePage.validate();

        // Assure not logged in status
        homePage.userMenu().validateNotLoggedIn();
        // Assure an empty cart
        homePage.miniCart().validateTotalCount(totalCount);
        homePage.miniCart().validateSubtotal("$0.00");
        final String oldSubtotal = homePage.miniCart().getSubtotal();

        // Goto login form
        loginPage = homePage.userMenu().openLogin();
        loginPage.validateStructure();
        homePage = loginPage.sendLoginform("john@doe.com", "topsecret");

        homePage.validateSuccessfulLogin("John");

        // Goto category
        final String categoryName = homePage.topNav().getSubCategoryNameByIndex(2, 3);
        categoryPage = homePage.topNav().clickSubCategoryByIndex(2, 3);
        categoryPage.validate(categoryName);

        // Goto product page
        final String productName = categoryPage.getProducNametByIndex(2, 1);
        productPage = categoryPage.clickProductByIndex(2, 1);
        productPage.validate(productName);

        productPage.addToCart("32 x 24 in", "matte");

        // Goto cart and validate
        final Product product = productPage.getProduct();
        cartPage = productPage.miniCart().openCartPage();
        cartPage.validateStructure();
        cartPage.validateShippingCosts(SHIPPINGCOSTS);
        cartPage.miniCart().validateMiniCart(1, product);
        cartPage.miniCart().validateTotalCount(++totalCount);
        cartPage.validateCartItem(0, product);
        cartPage.validateSubAndLineItemTotalAfterAdd(0, oldSubtotal, "$0.00");

        // TODO Change some steps form here on
        // setup checkout data
        final Address shippingAddress = new Address("Jimmy Blue", "Ochsenknecht Records", "6 Wall St", "Burlington", "Massachusetts", "01803", "United States");
        final boolean sameBillingAddress = false;
        final Address billingAddress = new Address("Jimmy Blue", "Ochsenknecht Records", "6 Wall St", "Burlington", "Massachusetts", "01803", "United States");
        final CreditCard creditcard = new CreditCard("Jimmy Blue", "4111111111111111", "xxxx xxxx xxxx 1111", "04", "2018");
        // Goto shipping address and validate
        shippingAddressPage = cartPage.openCheckoutPage();
        shippingAddressPage.validateStructure();

        // Send shipping address and validate billing form
        billingAddressPage = shippingAddressPage.sendShippingAddressForm(shippingAddress, sameBillingAddress);
        billingAddressPage.validateStructure();

        // Send billing address and validate payment form
        paymentPage = billingAddressPage.sendBillingAddressForm(billingAddress);
        paymentPage.validateStructure();

        // Send payment data and validate place order page
        placeOrderPage = paymentPage.sendPaymentForm(creditcard);
        placeOrderPage.validateStructure();
        placeOrderPage.validateProduct(0, product.getName(), product.getAmount(), product.getStyle(), product.getSize());
        placeOrderPage.validateAddressAndPayment(shippingAddress, billingAddress, creditcard);

        // Place order
        homePage = placeOrderPage.placeOrder();
        // Validate oorder confirmation on Homepage
        homePage.validate();
        homePage.validateSuccessfulOrder();
    }
}
