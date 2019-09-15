package mo.capture.webActivity.plugin.model;

public class SearchAction implements CapturableAndConvertibleToCSV {
    private String browser;
    private String pageUrl;
    private String pageTitle;
    private String search;
    private Long captureMilliseconds;

    @Override
    public String toCSV(String separator) {
        return this.browser + separator + this.pageUrl + separator + this.pageTitle + separator +
                this.search + separator + this.captureMilliseconds;
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

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
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
