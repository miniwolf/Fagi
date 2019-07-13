package com.fagi.guitests;

import com.fagi.controller.MainScreen;
import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.helpers.WaitForFXEventsTestHelper;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.network.InputHandler;
import com.fagi.responses.AllIsWell;
import com.fagi.testfxExtension.FagiNodeFinderImpl;
import com.fagi.threads.ThreadPool;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.api.FxService;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;

@ExtendWith(ApplicationExtension.class)
public class SignOutTests {
    private final ThreadPool threadPool = new ThreadPool();

    @BeforeAll
    public static void initialize() {
        System.out.println("Starting SignOutTests");
        FxAssert.assertContext().setNodeFinder(new FagiNodeFinderImpl(FxService.serviceContext().getWindowFinder()));
    }

    @Test
    public void clickOnToggleSignOut_WillChangeVisibilityOnDropdownId(FxRobot robot) {
        FxAssert.verifyThat(
                "#dropdown",
                NodeMatchers.isInvisible());

        WaitForFXEventsTestHelper.clickOn(robot, ".gb_b");
        FxAssert.verifyThat(
                "#dropdown",
                NodeMatchers.isVisible());

        WaitForFXEventsTestHelper.clickOn(robot, ".gb_b");
        FxAssert.verifyThat(
                "#dropdown",
                NodeMatchers.isInvisible());
    }

    @Test
    public void clickOnSignOut_WillStartTheLoginScreen(FxRobot robot) {
        WaitForFXEventsTestHelper.clickOn(robot, ".gb_b");
        WaitForFXEventsTestHelper.clickOn(robot, ".gb_Fa");

        FxAssert.verifyThat(
                "#UniqueLoginScreen",
                NodeMatchers.isNotNull(),
                builder -> builder.append("Should got to the login screen view.")
        );
    }

    @Start
    public void start(Stage stage) {
        var communication = Mockito.mock(Communication.class);
        var inputHandler = Mockito.mock(InputHandler.class);
        Mockito.when(communication.getNextResponse()).thenReturn(new AllIsWell());
        var fagiApp = Mockito.mock(FagiApp.class);

        Mockito.doCallRealMethod().when(communication).setInputHandler(inputHandler);
        Mockito.doCallRealMethod().when(communication).getInputDistributor();
        Mockito.doCallRealMethod().when(inputHandler).setupDistributor(Mockito.any());
        Mockito.doCallRealMethod().when(inputHandler).addIngoingMessage(Mockito.any());
        Mockito.doCallRealMethod().when(inputHandler).getDistributor();

        communication.setInputHandler(inputHandler);
        inputHandler.setupDistributor(threadPool);

        ChatManager.setCommunication(communication);
        ChatManager.setApplication(fagiApp);

        stage.setScene(new Scene(new AnchorPane()));
        var test = new MainScreen("Test", communication, stage);
        test.initCommunication(threadPool);

        var draggable = new Draggable(stage);
        var scene = new Scene(test);
        Mockito.doAnswer(invocationOnMock -> {
            stage.setScene(scene);
            var masterLogin = new MasterLogin(fagiApp, communication, stage, draggable);
            masterLogin.showMasterLoginScreen();
            stage.show();
            return masterLogin;
        }).when(fagiApp).showLoginScreen();
        stage.setScene(scene);
        stage.show();
    }
}
