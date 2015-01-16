package com.alizarion.crossview.entities;

import com.alizarion.reference.location.entities.ElectronicAddress;
import com.alizarion.reference.security.entities.Role;
import com.alizarion.reference.security.entities.RoleKey;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.imageio.ImageIO;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author selim@openlinux.fr.
 */
public class CrossviewEntitiesTest {

    private Account selim;

    private Account cecile;

    private Account kadour;

    private Set<Role> startRoles = new HashSet<>();

    private EntityManager em;

    private EntityManagerFactory emf;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    @SuppressWarnings("unchecked")
    public void init() throws MalformedURLException, URISyntaxException, AddressException {
        emf = Persistence.createEntityManagerFactory("crossViewTestPu");
        em = emf.createEntityManager();
        EntityTransaction trx = em.getTransaction();
        trx.begin();
        Set<Role> roles =  new HashSet<Role>(em.createQuery("select role from Role role ").getResultList());
        if(roles.isEmpty()) {
            Role role = em.merge(new Role(new RoleKey("USER", "user", "all user have this role")));
            this.startRoles.add(role);
        } else {
            this.startRoles =roles;
        }
        trx.commit();

        //creation du compte utilisateur 1
        selim = new Account("alizarion","mdp",new ElectronicAddress(new InternetAddress("selim@openlinux.fr")),this.startRoles);

        cecile  = new Account("titoune","mdp",new ElectronicAddress(new InternetAddress("cecile@openlinux.fr")), this.startRoles);

        kadour = new Account("kadi","mdp",new ElectronicAddress(new InternetAddress("kadi@openlinux.fr")), this.startRoles);


        //  personnalisation du profil
        selim.getProfile().setDescription("blablabla");
        cecile.getProfile().setDescription("blblablabla");



    }

    @After
    public void tearDown(){
        this.em.close();
        this.emf.close();
    }

    @Test
    public void singInTest(){
        Assert.assertTrue(cecile.getCredential().isCorrectPassword("mdp"));
        Assert.assertTrue(selim.getCredential().isCorrectPassword("mdp"));
        Assert.assertEquals(cecile.getCredential().getRoles(), selim.getCredential().getRoles());
    }

    @Test
    public void persistAccount() throws AddressException {
       /*EntityTransaction transaction =  this.em.getTransaction() ;
        transaction.begin();
        cecile =this.em.merge(cecile);
        transaction.commit();
        Assert.assertNotNull(cecile.getId());
        Assert.assertNotNull(cecile.getCredential().getId());
        Assert.assertNotNull(cecile.getCredential().getPerson().getId());
       Assert.assertNotNull(cecile.getCredential().getPerson().getPrimaryElectronicAddress().getId()); */


    }

    @Test
    public void publicationAndNotificationsWithPersisting() throws IOException, URISyntaxException {
        EntityTransaction trx =  em.getTransaction() ;
        trx.begin();
        cecile = em.merge(cecile);
        selim = em.merge(selim);
        kadour = em.merge(kadour);
        em.flush();


        cecile.getUser().publish(new Publication(em.merge(new WebContent.Builder(new URL("http://cecile.com"),"site de cecile").build())));

        selim.getUser().relay(cecile.getUser().getUserPublicationsAsList().get(0));


        kadour.getUser().follow(selim.getUser());
        em.flush();


        org.junit.Assert.assertTrue(selim.getUser().getSubscriptionStream().size() == 0);
        org.junit.Assert.assertTrue(selim.getUser().getStream().size() == 1);


        int tutu =  cecile.getUser().getSubscriptionStream().size();
        org.junit.Assert.assertTrue(cecile.getUser().getSubscriptionStream().size() == 0);
        org.junit.Assert.assertTrue(cecile.getUser().getStream().size() == 1);

        org.junit.Assert.assertTrue(kadour.getUser().getSubscriptionStream().size() == 1);
        org.junit.Assert.assertTrue(kadour.getUser().getStream().size() == 0);



        cecile.getUser().follow(selim.getUser());


        org.junit.Assert.assertTrue(selim.getUser().getSubscriptionStream().size() == 0);
        org.junit.Assert.assertTrue(selim.getUser().getStream().size() == 1);

        org.junit.Assert.assertTrue(cecile.getUser().getSubscriptionStream().size() == 1);
        org.junit.Assert.assertTrue(cecile.getUser().getStream().size() == 1);

        org.junit.Assert.assertTrue(kadour.getUser().getSubscriptionStream().size() == 1);
        org.junit.Assert.assertTrue(kadour.getUser().getStream().size() == 0);


        Publication publishing =  new Publication(em.merge(new WebContent.Builder(new URL("http://selim.com"),"site de selim").build()));
        selim.getUser().publish(publishing);

        org.junit.Assert.assertTrue(selim.getUser().getSubscriptionStream().size() == 0);
        org.junit.Assert.assertTrue(selim.getUser().getStream().size() == 2);

        org.junit.Assert.assertTrue(cecile.getUser().getSubscriptionStream().size() == 2);
        org.junit.Assert.assertTrue(cecile.getUser().getStream().size() == 1);

        org.junit.Assert.assertTrue(kadour.getUser().getSubscriptionStream().size() == 2);
        org.junit.Assert.assertTrue(kadour.getUser().getStream().size() == 0);

        em.flush();
        em.clear();
        trx.commit();
        trx.begin();

      /*  em.refresh(cecile);
        em.refresh(selim);
        em.refresh(kadour);*/
        kadour =  em.find(Account.class,kadour.getId());

        cecile =  em.find(Account.class,cecile.getId());

        selim =  em.find(Account.class,selim.getId());


        Publication publication = new Publication(em.merge(new WebContent.Builder(new URL("http://selim2.com"),"site de selim 2").build()));
        selim.getUser().publish(publication);
        //      em.flush();

        org.junit.Assert.assertTrue(selim.getUser().getSubscriptionStream().size() == 0);
        org.junit.Assert.assertTrue(selim.getUser().getStream().size() == 3);

        int toot = kadour.getUser().getSubscriptionStream().size();
        org.junit.Assert.assertTrue(cecile.getUser().getSubscriptionStream().size() == 3);
        org.junit.Assert.assertTrue(cecile.getUser().getStream().size() == 1);

        org.junit.Assert.assertTrue(kadour.getUser().getSubscriptionStream().size() == 3);
        org.junit.Assert.assertTrue(kadour.getUser().getStream().size() == 0);


        selim.getUser().unPublish(selim.getUser().getUserPublicationsAsList().get(0));
        //    selim.getUser().follow(cecile.getUser());
        em.flush();

        trx.commit();
        trx.begin();
        cecile =  em.find(Account.class,cecile.getId());
        selim =  em.find(Account.class,selim.getId());
        kadour =  em.find(Account.class,kadour.getId());

        //
        // em.clear();
        org.junit.Assert.assertTrue(selim.getUser().getSubscriptionStream().size() == 0);
        org.junit.Assert.assertTrue(selim.getUser().getStream().size() == 2);

        org.junit.Assert.assertTrue(cecile.getUser().getSubscriptionStream().size() == 2);
        org.junit.Assert.assertTrue(cecile.getUser().getStream().size() == 1);

        org.junit.Assert.assertTrue(kadour.getUser().getSubscriptionStream().size() == 2);
        org.junit.Assert.assertTrue(kadour.getUser().getStream().size() == 0);

        selim.getUser().follow(cecile.getUser());

        org.junit.Assert.assertTrue(selim.getUser().getSubscriptionStream().size() == 1);
        org.junit.Assert.assertTrue(selim.getUser().getStream().size() == 2);

        org.junit.Assert.assertTrue(cecile.getUser().getSubscriptionStream().size() == 2);
        org.junit.Assert.assertTrue(cecile.getUser().getStream().size() == 1);

        org.junit.Assert.assertTrue(kadour.getUser().getSubscriptionStream().size() == 2);
        org.junit.Assert.assertTrue(kadour.getUser().getStream().size() == 0);

        kadour.getUser().unFollow(selim.getUser());

        em.flush();
        cecile =  em.find(Account.class,cecile.getId());
        selim =  em.find(Account.class,selim.getId());
        kadour =  em.find(Account.class,kadour.getId());


        org.junit.Assert.assertTrue(selim.getUser().getSubscriptionStream().size() == 1);
        org.junit.Assert.assertTrue(selim.getUser().getStream().size() == 2);

        org.junit.Assert.assertTrue(cecile.getUser().getSubscriptionStream().size() == 2);
        org.junit.Assert.assertTrue(cecile.getUser().getStream().size() == 1);

        org.junit.Assert.assertTrue(kadour.getUser().getSubscriptionStream().size() == 0);
        org.junit.Assert.assertTrue(kadour.getUser().getStream().size() == 0);


        selim.printNotifications();


        cecile.printNotifications();
        //cecile.getUser().getUserPublicationsAsList().get(0).allege(selim.getUser().getUserPublicationsAsList().get(0));
        //cecile.getUser().getUserPublicationsAsList().get(0).refute(selim.getUser().getUserPublicationsAsList().get(0));

        Publication publicationToCross = cecile.getUser().getUserPublicationsAsList().get(0);
        Publication publicationToCrossWith =selim.getUser().getUserPublicationsAsList().get(0);
        publicationToCross.crossView(publicationToCrossWith,kadour.getUser());
        em.merge(publicationToCross);
        em.merge(cecile);
        em.merge(selim);
        em.merge(kadour);
        em.flush();
        em.refresh(publicationToCross);
        em.refresh(publicationToCrossWith);

        System.out.print("content "+publicationToCross.getContent().getContentId()+" has relation with");
        for(Map.Entry<Publication,Set<User>> entry: publicationToCross.getAllRelatedContents().entrySet()) {
            System.out.println("with" + entry.getKey().getContent().getContentId());
        }
       // em.detach(publicationToCrossWith);

        CrossLink linktoremove = publicationToCross.unCrossView(publicationToCrossWith,kadour.getUser());
        linktoremove.preRemove();
        em.remove(linktoremove);
        em.flush();
        em.clear();

        publicationToCross = em.find(Publication.class,publicationToCross.getId());

        publicationToCrossWith = em.find(Publication.class,publicationToCrossWith.getId());
        System.out.print("content "+publicationToCross.getContent().getContentId()+" has relation with");
        for(Map.Entry<Publication,Set<User>> entry: publicationToCross.getAllRelatedContents().entrySet()) {
            System.out.println("with" + entry.getKey().getContent().getContentId());
        }
        em.flush();

        selim.printNotifications();


        cecile.printNotifications();
        em.clear();

         WebContent content = (WebContent) em.find(PublicationContent.class,publication.getContent().getContentId());
        em.clear();
        System.out.print("publication count" + content.getPublicationCount());
        System.out.print("trasaction :" + trx.isActive());
        trx.commit();
    }

    public File drawImage() throws IOException {
        BufferedImage bufferedImage = new BufferedImage(200,200 ,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D =  bufferedImage.createGraphics();
        for(int i = 0 ; i < 200 ; i += 20){
            graphics2D.drawString("Test file management",20,i);

        }
        File file =  new File(this.temporaryFolder.newFolder(),"mon_image.jpg");
        try {
            ImageIO.write(bufferedImage, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


}
