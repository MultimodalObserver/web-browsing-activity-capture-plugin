package mo.capture.webActivity.plugin.model;

import java.util.Map;

public class CSVHeader {

    public static final Map<String, String[]> HEADERS;
    static {
        HEADERS = Map.of("mouseClicks", new String[]{"browser", "pageUrl", "pageTitle", "xPage", "yPage", "xClient", "yClient", "xScreen", "yScreen", "button", "captureMilliseconds"},
                "mouseMoves", new String[]{"browser", "pageUrl", "pageTitle", "xPage", "yPage", "xClient", "yClient", "xScreen", "yScreen", "xMovements", "yMovement", "captureMilliseconds"},
                "keystrokes", new String[]{"browser", "pageUrl", "pageTitle", "keyValue", "captureMilliseconds"},
                "mouseUps", new String[]{"browser", "pageUrl", "pageTitle", "selectedText", "captureMilliseconds"},
                "tabs", new String[]{"browser", "tabUrl", "tabTitle", "actionType", "tabIndex", "tabId", "windowId", "captureMilliseconds"},
                "searchs", new String[]{"browser", "pageUrl", "pageTitle", "search", "captureMilliseconds"});
    }
}
