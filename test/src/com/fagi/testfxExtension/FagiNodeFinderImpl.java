package com.fagi.testfxExtension;

import javafx.stage.Window;
import org.testfx.service.finder.WindowFinder;
import org.testfx.service.finder.impl.NodeFinderImpl;
import org.testfx.service.query.NodeQuery;
import org.testfx.util.NodeQueryUtils;

import java.util.List;

public class FagiNodeFinderImpl extends NodeFinderImpl {
    private final WindowFinder windowFinder;

    public FagiNodeFinderImpl(WindowFinder windowFinder) {
        super(windowFinder);
        this.windowFinder = windowFinder;
    }

    @Override
    public NodeQuery fromAll() {
        List<Window> windows = this.windowFinder.listTargetWindows();
        var rootsOfWindows = NodeQueryUtils.rootsOfWindows(windows);
        return (new FagiNodeQueryImpl()).from(rootsOfWindows);
    }
}
