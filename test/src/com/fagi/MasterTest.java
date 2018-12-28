package com.fagi;

import com.fagi.action.items.LoadFXMLTest;
import com.fagi.guitests.GuiTestSuite;
import com.fagi.network.CommunicationTest;
import com.fagi.util.UtilsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoadFXMLTest.class,
        GuiTestSuite.class,
        CommunicationTest.class,
        UtilsTest.class
})
public final class MasterTest {} // or ModuleFooSuite, and that in AllTests