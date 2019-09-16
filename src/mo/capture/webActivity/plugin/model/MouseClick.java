package mo.capture.webActivity.plugin.model;

public class MouseClick implements CapturableAndConvertibleToCSV {
    private String browser;
    private String pageUrl;
    private String pageTitle;
    private Long xPage;
    private Long yPage;
    private Long xClient;
    private Long yClient;
    private Long xScreen;
    private Long yScreen;
    private Long xMovement;
    private Long yMovement;
    private Integer button;
    private Long captureMilliseconds;

    public MouseClick(){

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

    public Long getxPage() {
        return xPage;
    }

    public void setxPage(Long xPage) {
        this.xPage = xPage;
    }

    public Long getyPage() {
        return yPage;
    }

    public void setyPage(Long yPage) {
        this.yPage = yPage;
    }

    public Long getxClient() {
        return xClient;
    }

    public void setxClient(Long xClient) {
        this.xClient = xClient;
    }

    public Long getyClient() {
        return yClient;
    }

    public void setyClient(Long yClient) {
        this.yClient = yClient;
    }

    public Long getxScreen() {
        return xScreen;
    }

    public void setxScreen(Long xScreen) {
        this.xScreen = xScreen;
    }

    public Long getyScreen() {
        return yScreen;
    }

    public void setyScreen(Long yScreen) {
        this.yScreen = yScreen;
    }

    public Long getxMovement() {
        return xMovement;
    }

    public void setxMovement(Long xMovement) {
        this.xMovement = xMovement;
    }

    public Long getyMovement() {
        return yMovement;
    }

    public void setyMovement(Long yMovement) {
        this.yMovement = yMovement;
    }

    @Override
    public String toCSV(String separator) {
        return this.browser + separator + this.pageUrl + separator + this.pageTitle + separator + this.xPage
                + separator + this.yPage + separator + this.xClient + separator + this.yClient + separator +
                this.xScreen + separator + this.yScreen + separator + this.xMovement + separator +
                this.yMovement + separator + this.button + separator + this.captureMilliseconds;
    }

    @Override
    public Long getCaptureMilliseconds() {
        return captureMilliseconds;
    }

    @Override
    public void setCaptureMilliseconds(Long captureMilliseconds) {
        this.captureMilliseconds = captureMilliseconds;
    }

    public Integer getButton() {
        return button;
    }

    public void setButton(Integer button) {
        this.button = button;
    }
}
