/**
 * 
 */
package com.xceptance.neodymium.scripting.template.selenide;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.xceptance.neodymium.multibrowser.Browser;
import com.xceptance.neodymium.scripting.template.datapool.UserPool;
import com.xceptance.neodymium.scripting.template.selenide.flow.DeleteUserFlow;
import com.xceptance.neodymium.scripting.template.selenide.flow.OpenHomePageFlow;
import com.xceptance.neodymium.scripting.template.selenide.objects.User;
import com.xceptance.neodymium.scripting.template.selenide.page.browsing.HomePage;
import com.xceptance.neodymium.scripting.template.selenide.page.user.LoginPage;
import com.xceptance.neodymium.scripting.template.selenide.page.user.RegisterPage;
import com.xceptance.neodymium.scripting.template.selenide.tests.BasicTest;

/**
 * @author pfotenhauer
 */
@Browser(
{
  "Chrome_1024x768"
})
public class TRegisterV3 extends BasicTest
{
    public User user;

    @Before
    public void setup()
    {
        user = new UserPool().getRandomEntry();
    }

    @Test
    public void test()
    {
        // Page types to use
        HomePage homePage;
        LoginPage loginPage;
        RegisterPage registerPage;

        // Goto homepage
        homePage = new OpenHomePageFlow().flow();
        homePage.validate();

        // Assure not logged in status
        homePage.userMenu().validateNotLoggedIn();

        // Goto login form
        loginPage = homePage.userMenu().openLogin();
        loginPage.validateStructure();

        // Goto register form
        registerPage = loginPage.openRegister();
        registerPage.validateStructure();

        loginPage = registerPage.sendRegisterForm(user, user.getPassword());
        loginPage.validateSuccessfulLRegistration();
        loginPage.validateStructure();

        homePage = loginPage.sendLoginform(user);
        homePage.validateSuccessfulLogin(user);
    }

    @After
    public void after()
    {
        new DeleteUserFlow(user).flow();
    }
}