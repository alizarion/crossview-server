package com.alizarion.crossview.service;

import com.alizarion.crossview.dto.HooverMessageDTO;
import com.alizarion.crossview.entities.WebContent;
import com.alizarion.crossview.entities.WebHost;
import com.alizarion.crossview.exception.ParsingWebContentException;
import com.google.common.net.InternetDomainName;
import de.jetwick.snacktory.HtmlFetcher;
import de.jetwick.snacktory.JResult;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.jms.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author selim@openlinux.fr.
 */
@JMSDestinationDefinitions(
        value =  {
                @JMSDestinationDefinition(
                        name = "java:/queue/HOOVERMDBQueue",
                        interfaceName = "javax.jms.Queue",
                        destinationName = "HooverMDBQueue"
                )
        }
)
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class HooverService implements Serializable {

    private static final long serialVersionUID = 9192254877039188210L;

    @PersistenceContext
    EntityManager em;

    @Inject
    private JMSContext context;

    @Resource(lookup = "java:/queue/HOOVERMDBQueue")
    private Queue hoover;

    public HooverService() {
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    private URL getFollowRedirectUrl(URL url)   {
        try {
            URLConnection con = url.openConnection();
            con.setUseCaches(false);
            con.connect();
            InputStream is = con.getInputStream();
            is.close();
            return con.getURL();

        } catch (IOException e ){
            return url;
        }
    }

    public WebContent getWebContent(final URL url) throws ParsingWebContentException {
        //if exist return it
        URL webContentFinalUrl = getFollowRedirectUrl(url);



        WebContent webContent =  this.em.find(WebContent.class,webContentFinalUrl.toString());
        if (webContent != null){
            return webContent;
        } else {
            HtmlFetcher fetcher = new HtmlFetcher();
            fetcher = fetcher.clearCacheCounter();
            fetcher.setCache(null);
            try {
                JResult readability = fetcher.fetchAndExtract(webContentFinalUrl.toString(), 5000, true);

                WebHost  webHost = em.find(WebHost.class,webContentFinalUrl.toString()) ;
                if (webHost == null){
                    webHost  =  new WebHost(webContentFinalUrl.getHost());
                }
                //check rss link on the home page
                InternetDomainName topPrivateDomain =
                        InternetDomainName.from(webContentFinalUrl.getHost()).topPrivateDomain();

                URL homePageUrl = new URL(webContentFinalUrl.getProtocol(),
                        topPrivateDomain.toString(),"");
                URL homePageFinalUrl = getFollowRedirectUrl(homePageUrl);
                JResult homePageResult = fetcher.fetchAndExtract(
                        homePageFinalUrl.toString()
                        , 5000, true);
                String rss = homePageResult.getRssUrl() ;
                if (!rss.startsWith("http") && !rss.isEmpty()){
                    //is relative fucking path
                    if (rss.startsWith("/")) {
                        rss = homePageResult.getCanonicalUrl()+  rss;
                        
                    }else {
                        rss = homePageResult.getCanonicalUrl() + "/" + rss;
                    }
                }
                String faviconStringUrl =  readability.getFaviconUrl();
                if(!faviconStringUrl.startsWith("http") && !faviconStringUrl.isEmpty()){
                    if (readability.getFaviconUrl().startsWith("/")){
                        faviconStringUrl =  homePageResult.getCanonicalUrl() +  faviconStringUrl;

                    }  else {
                        faviconStringUrl =  homePageResult.getCanonicalUrl() + "/" +  faviconStringUrl;
                    }
                }

                String homeTitle = homePageResult.getTitle();
                //TODO get home page title and set it on webhost
                webContent =
                        new WebContent.Builder(
                                webContentFinalUrl,
                                readability.getTitle())
                                .setCanonicalUrl(readability.getCanonicalUrl())
                                .setDescription(readability.getDescription())
                                .setOriginalUrl(readability.getOriginalUrl())
                                .setText(readability.getText().length()> 25000 ?
                                        readability.getText().substring(0,25000):readability.getText())
                                .setRssUrl(rss)
                                .setFaviconUrl(faviconStringUrl)
                                .setHomeTitle(homeTitle)
                                .setImageUrl(readability.getImageUrl())
                                .setVideoUrl(readability.getVideoUrl())
                                .build();

                webContent = this.em.merge(webContent);
                this.em.flush();
                //TODO do not send readability object not full serializable

                context.createProducer().send((Destination)hoover
                        ,context.createObjectMessage(new HooverMessageDTO(webContent)));
                //  asyncHoover.getResources(webContent,readability);

                return webContent;

            } catch (Exception e) {
                e.printStackTrace();
                throw new ParsingWebContentException(url.toString() , e);
            }
        }



    }



}
