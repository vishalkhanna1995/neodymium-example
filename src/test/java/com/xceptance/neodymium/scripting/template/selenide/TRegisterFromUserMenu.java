/**
 * 
 */
package com.xceptance.neodymium.scripting.template.selenide;

import org.junit.After;
import org.junit.Test;

import com.xceptance.neodymium.scripting.template.selenide.flow.FDeleteUser;
import com.xceptance.neodymium.scripting.template.selenide.flow.FOpenHomepage;
import com.xceptance.neodymium.scripting.template.selenide.objects.User;
import com.xceptance.neodymium.scripting.template.selenide.page.PHome;
import com.xceptance.neodymium.scripting.template.selenide.page.user.PLogin;
import com.xceptance.neodymium.scripting.template.selenide.page.user.PRegister;

/**
 * @author pfotenhauer
 */
public class TRegisterFromUserMenu extends PureSelenideTest
{
    final User user = new User("Jane", "Doe", "jane@doe.com", "topsecret");

    @Test
    public void test()
    {
        // Page types to use
        PHome homePage;
        PLogin loginPage;
        PRegister registerPage;

        // Goto homepage
        homePage = new FOpenHomepage().flow();
        homePage.validate();

        // Assure not logged in status
        homePage.userMenu().validateNotLoggedIn();

        // Goto register form
        registerPage = homePage.userMenu().openRegister();
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
        new FDeleteUser(user).flow();
    }
}
