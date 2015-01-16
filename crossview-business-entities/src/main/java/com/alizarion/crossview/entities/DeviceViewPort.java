package com.alizarion.crossview.entities;

/**
 * @author selim@openlinux.fr
 */
public enum DeviceViewPort {

    M(DeviceUserAgent.M,500),
    T(DeviceUserAgent.M,1000),
    D(DeviceUserAgent.D, 1440);

    private DeviceUserAgent userAgent;

    private Integer viewPort;

    DeviceViewPort(DeviceUserAgent userAgent,Integer viewPort) {
        this.userAgent = userAgent;
        this.viewPort = viewPort;
    }

    public Integer getViewPort() {
        return viewPort;
    }

    public void setViewPort(Integer viewPort) {
        this.viewPort = viewPort;
    }

    public DeviceUserAgent getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(DeviceUserAgent userAgent) {
        this.userAgent = userAgent;
    }

    public enum DeviceUserAgent{
        M("Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X)" +
                " AppleWebKit/534.46 (KHTML, like Gecko)" +
                " Version/5.1 Mobile/9A334 Safari/7534.48.3"),
        D("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5)" +
                " AppleWebKit/537.36 (KHTML, like Gecko)" +
                " Chrome/39.0.2171.71 Safari/537.36");

        private String userAgent;

        DeviceUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public String getUserAgent() {
            return userAgent;
        }
    }
}
