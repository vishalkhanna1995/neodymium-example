package com.xceptance.neodymium.scripting.template.selenide;

import org.junit.Test;

import com.xceptance.multibrowser.TestTargets;
import com.xceptance.neodymium.scripting.template.selenide.flow.FOpenHomepage;
import com.xceptance.neodymium.scripting.template.selenide.page.PHome;

@TestTargets(
{
  "Chrome_1024x768", "FF_1024x768"
})
public class TVisit extends BasicTest
{
    @Test
    public void test()
    {
        PHome homePage = new FOpenHomepage().flow();
        homePage.validateStructure();
        homePage.footer().validate();
    }
}
