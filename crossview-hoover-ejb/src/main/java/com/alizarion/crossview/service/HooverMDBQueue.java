package com.alizarion.crossview.service;

import com.alizarion.crossview.entities.DeviceViewPort;
import com.alizarion.crossview.entities.WebContent;
import com.alizarion.crossview.entities.WebContentCache;
import com.alizarion.crossview.entities.WebContentCacheID;
import com.alizarion.crossview.mbean.CrossViewConfigMBean;
import com.alizarion.crossview.mbean.PhantomJSMbean;
import com.alizarion.reference.exception.ApplicationException;
import com.alizarion.reference.filemanagement.tools.ImageFileHelper;
import de.jetwick.snacktory.JResult;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.*;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.net.URL;

/**
 * @author selim@openlinux.fr.
 */
@MessageDriven(name = "HooverQueueMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/HOOVERMDBQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty( propertyName = "maxSession", propertyValue = "4")
})
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class HooverMDBQueue implements MessageListener {

    @EJB
    private CrossViewConfigMBean config;

    @EJB
    private PhantomJSMbean phantomJSConfig;


    @PersistenceContext
    EntityManager em;

    public void getResources(WebContent webContent,final JResult readability)
            throws ApplicationException, IOException {
        if (!StringUtils.isEmpty(readability.getImageUrl())){
            try {
                webContent.setImage(ImageFileHelper.getAndManageImage(
                        this.em,
                        new URL(readability.getImageUrl()),
                        this.config.getManagedFileRootFolder().toString()));

            } catch (IOException | ApplicationException ignored) {
            }
        }
        if (!StringUtils.isEmpty(readability.getFaviconUrl())
                && webContent.getWebHost().getFaviconImage() ==null){
            try {
                webContent.
                        getWebHost().
                        setFaviconImage(ImageFileHelper.
                                getAndManageImage(this.em,
                                        new URL(readability.getFaviconUrl()),
                                        this.config.getManagedFileRootFolder().toString()));

            } catch (IOException | ApplicationException ignored) {
            }
        }

        for (DeviceViewPort viewPort :  DeviceViewPort.values()){
            WebContentCache cache  =
                    new WebContentCache(new WebContentCacheID(viewPort,webContent));
            cache.setCacheImage(ImageFileHelper
                    .getAndManageImage(this.em,
                            PhantomJSHelper.getScreenShot(
                                    phantomJSConfig.getPathToPhantomExecutable(),
                                    webContent.getContentIdAsUrl(),
                                    viewPort).toURI().toURL(),
                            this.config.getManagedFileRootFolder().toString()));
            webContent.addCache(cache);
        }

        em.merge(webContent);
        em.flush();

    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage){
            try {
                if (((ObjectMessage) message).getObject() instanceof JResult){
                    JResult readability  =  (JResult)((ObjectMessage) message).getObject();
                    try {
                        WebContent webContent =  this.em.find(
                                WebContent.class,
                                readability.getUrl());
                        this.em.clear();
                        this.em.flush();
                        if (webContent!= null) {
                            getResources(webContent, readability);
                        }
                    } catch (ApplicationException | IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
