package com.fagi.guitests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CreatePasswordTests.class,
        CreateUserNameTests.class,
        FriendsTests.class,
        InviteCodeTests.class,
        LoginTests.class,
        SearchUserTests.class,
        SignOutTests.class})
public class GuiTestSuite {
}
