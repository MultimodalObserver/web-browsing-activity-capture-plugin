package mo.capture.webActivity.plugin.model;

import java.util.Map;

public class CSVHeader {

    public static final Map<String, String[]> HEADERS;
    static {
        HEADERS = Map.of("mouseClicks", new String[]{"browser", "pageUrl", "pageTitle", "xPage", "yPage", "xClient", "yClient", "xScreen", "yScreen","button", "captureTimestamp"},
                "mouseMoves", new String[]{"browser", "pageUrl", "pageTitle", "xPage", "yPage", "xClient", "yClient", "xScreen", "yScreen", "xMovements", "yMovement", "captureTimestamp"},
                "keystrokes", new String[]{"browser", "pageUrl", "pageTitle", "keyValue", "captureTimestamp"},
                "mouseUps", new String[]{"browser", "pageUrl", "pageTitle", "selectedText", "captureTimestamp"},
                "tabs", new String[]{"browser", "tabUrl", "tabTitle", "actionType", "tabIndex", "tabId", "windowId", "captureTimestamp"},
                "searchs", new String[]{"browser", "pageUrl", "pageTitle", "search", "captureTimestamp"});
    }
}
