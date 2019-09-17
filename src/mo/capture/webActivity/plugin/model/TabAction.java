package mo.capture.webActivity.plugin.model;

public class TabAction implements CapturableAndConvertibleToCSV {

    private String browser;
    private String tabUrl;
    private String tabTitle;
    private String actionType;
    private Integer tabIndex;
    private Integer tabId;
    private Integer windowId;
    private Long captureMilliseconds;

    public TabAction(){

    }

    @Override
    public String toCSV(String separator) {
        return this.browser + separator + this.tabUrl + separator + this.tabTitle + separator +
                this.actionType + separator + this.tabIndex + separator +
                this.tabId + separator + this.windowId + separator + captureMilliseconds;
    }


    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getTabUrl() {
        return tabUrl;
    }

    public void setTabUrl(String tabUrl) {
        this.tabUrl = tabUrl;
    }

    public String getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Integer getTabId() {
        return tabId;
    }

    public void setTabId(Integer tabId) {
        this.tabId = tabId;
    }

    public Integer getWindowId() {
        return windowId;
    }

    public void setWindowId(Integer windowId) {
        this.windowId = windowId;
    }

    @Override
    public Long getCaptureMilliseconds() {
        return captureMilliseconds;
    }

    @Override
    public void setCaptureMilliseconds(Long captureMilliseconds) {
        this.captureMilliseconds = captureMilliseconds;
    }

    public Integer getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }
}
