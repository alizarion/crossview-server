package com.alizarion.crossview.service;

import com.google.common.net.InternetDomainName;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author selim@openlinux.fr.
 */
public class RssParserTest {

   //  @Test
    public void testParser() throws IOException, FeedException {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(new URL("http://korben.info/feed")));

        System.out.println(feed);
    }

    @Test
    public void DomainParser() throws MalformedURLException {
       InternetDomainName topPrivateDomain=  InternetDomainName.from("modqsd.dqsd.korben.info").topPrivateDomain();
       URL url = new URL("https://twitter.com/mathieusicard/status/546773735458758656?s=09");

    }
}

