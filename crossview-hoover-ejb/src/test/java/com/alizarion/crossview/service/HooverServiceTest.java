package com.alizarion.crossview.service;

import com.alizarion.crossview.entities.WebContent;
import com.alizarion.crossview.mbean.CrossViewConfigMBean;
import com.alizarion.crossview.mbean.PhantomJSMbean;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.net.URL;

@RunWith(MockitoJUnitRunner.class)
public class HooverServiceTest {

    private EntityManager em;
    private EntityManagerFactory emf;

    @Mock
    CrossViewConfigMBean config;

    @Mock
    PhantomJSMbean phantomJSConfig;

    @Mock
    HooverMDBQueue asyncHooverService;

    @InjectMocks
    HooverService hooverService;

    final static String PATH_TO_PHANTOM =  "/opt/local/bin/phantomjs";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        this.emf = Persistence.createEntityManagerFactory("crossViewTestPu");
        this.em = this.emf.createEntityManager();
        hooverService.setEm(this.em);
        Mockito.when(phantomJSConfig.
                getPathToPhantomExecutable()).
                thenReturn(PATH_TO_PHANTOM);

        Mockito.when(config.getManagedFileRootFolder())
                .thenReturn(tempFolder.getRoot().toURI());
    }

    @After
    public void tearDown() throws Exception {
        this.em.close();
        this.emf.close();
    }

    @Test
    public void testGetWebContent() throws Exception {
        EntityTransaction trx =  this.em.getTransaction();
        trx.begin();
        WebContent webContent = hooverService.getWebContent(new URL("http://www.grafikart.fr/blog/podcast-metier-developpeur"));
        Assert.assertNotNull(webContent);
        trx.commit();
    }

    @Test
    public void testGetResources() throws Exception {


    }
}