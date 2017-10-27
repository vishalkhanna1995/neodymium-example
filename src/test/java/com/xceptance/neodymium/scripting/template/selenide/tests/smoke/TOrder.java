/**
 * 
 */
package com.xceptance.neodymium.scripting.template.selenide.tests.smoke;

import org.junit.Test;

import com.xceptance.neodymium.multibrowser.Browser;
import com.xceptance.neodymium.scripting.template.selenide.flow.OpenHomePageFlow;
import com.xceptance.neodymium.scripting.template.selenide.objects.Address;
import com.xceptance.neodymium.scripting.template.selenide.objects.CreditCard;
import com.xceptance.neodymium.scripting.template.selenide.objects.Product;
import com.xceptance.neodymium.scripting.template.selenide.page.browsing.CategoryPage;
import com.xceptance.neodymium.scripting.template.selenide.page.browsing.HomePage;
import com.xceptance.neodymium.scripting.template.selenide.page.browsing.ProductdetailPage;
import com.xceptance.neodymium.scripting.template.selenide.page.checkout.BillingAddressPage;
import com.xceptance.neodymium.scripting.template.selenide.page.checkout.CartPage;
import com.xceptance.neodymium.scripting.template.selenide.page.checkout.PaymentPage;
import com.xceptance.neodymium.scripting.template.selenide.page.checkout.PlaceOrderPlace;
import com.xceptance.neodymium.scripting.template.selenide.page.checkout.ShippingAddressPage;
import com.xceptance.neodymium.scripting.template.selenide.page.user.LoginPage;
import com.xceptance.neodymium.scripting.template.selenide.tests.BasicTest;

/**
 * @author pfotenhauer
 */
@Browser(
{
  "Chrome_1024x768"
})
public class TOrder extends BasicTest
{
    @Test
    public void test()
    {
        // TODO put in place of first usage
        // Page types to use
        HomePage homePage;
        LoginPage loginPage;
        CategoryPage categoryPage;
        ProductdetailPage productPage;
        CartPage cartPage;
        ShippingAddressPage shippingAddressPage;
        BillingAddressPage billingAddressPage;
        PaymentPage paymentPage;
        PlaceOrderPlace placeOrderPage;

        // total product count will be updated throughout the test
        int totalCount = 0;

        // Goto homepage
        homePage = new OpenHomePageFlow().flow();
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
        final String productName = categoryPage.getProductNameByPosition(2, 1);
        productPage = categoryPage.clickProductByPosition(2, 1);
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

        // Goto shipping address and validate
        shippingAddressPage = cartPage.openShippingPage();
        shippingAddressPage.validateStructure();

        // Send shipping address and validate billing form
        billingAddressPage = shippingAddressPage.selectShippingAddress(0);
        billingAddressPage.validateStructure();

        // Send billing address and validate payment form
        paymentPage = billingAddressPage.selectBillingAddress(0);
        paymentPage.validateStructure();

        // setup checkout data for validation
        final Address shippingAddress = new Address("John Doe", "John Doe Inc.", "5-7 John Doe street", "New York", "NY", "12345", "United States");
        final Address billingAddress = new Address("John Doe", "John Doe Inc.", "5-7 John Doe street", "New York", "NY", "12345", "United States");
        final CreditCard creditcard = new CreditCard("John Doe", "4111111111111111", "xxxx xxxx xxxx 1111", "08", "2022");

        // Send payment data and validate place order page
        placeOrderPage = paymentPage.selectCreditCard(0);
        placeOrderPage.validateStructure();
        placeOrderPage.validateProduct(0, product.getName(), product.getAmount(), product.getStyle(), product.getSize());
        placeOrderPage.validateAddressAndPayment(shippingAddress, billingAddress, creditcard);

        // Place order
        homePage = placeOrderPage.placeOrder();
        // Validate order confirmation on Homepage
        homePage.validate();
        homePage.validateSuccessfulOrder();
    }
}