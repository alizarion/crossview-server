package com.alizarion.crossview.service;

import com.alizarion.crossview.entities.DeviceViewPort;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * @author selim@openlinux.fr.
 */
public  class PhantomJSHelper {

    public static File getScreenShot(final String pathToPhantom,
                                                 final URL url,
                                                 final DeviceViewPort viewPort){
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                pathToPhantom);
        caps.setJavascriptEnabled(true);
        caps.setCapability("takesScreenshot", true);
        caps.setCapability("phantomjs.page.settings.userAgent",
                viewPort.getUserAgent().getUserAgent());
        caps.setCapability("ignoreZoomSetting",false);
        PhantomJSDriver driver = new PhantomJSDriver(caps);
        driver.manage().window().setSize(
                new Dimension(viewPort.getViewPort(),
                        viewPort.getViewPort() * 2));

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(url.toString());
        File screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        driver.quit();

        return screenShot;
    }
}
