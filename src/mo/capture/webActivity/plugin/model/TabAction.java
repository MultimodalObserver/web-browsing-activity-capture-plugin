package mo.capture.webActivity.plugin.model;

public class TabAction implements CapturableAndConvertibleToCSV {

    private String browser;
    private String pageUrl;
    private String pageTitle;
    private String type;
    private Integer tabId;
    private Integer windowId;
    private Long captureMilliseconds;

    public TabAction(){

    }

    @Override
    public String toCSV(String separator) {
        return this.type + separator + this.tabId + separator + this.windowId + separator + captureMilliseconds;
    }


    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
