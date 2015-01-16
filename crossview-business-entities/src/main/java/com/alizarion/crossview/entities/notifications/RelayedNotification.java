package com.alizarion.crossview.entities.notifications;

import com.alizarion.crossview.entities.Account;
import com.alizarion.reference.social.entities.notification.Notification;
import com.alizarion.reference.social.entities.notification.Observer;
import com.alizarion.reference.social.entities.notification.Subject;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author selim@openlinux.fr.
 */
@Entity
@DiscriminatorValue(value = RelayedNotification.TYPE)
public class RelayedNotification extends Notification<Account> {

    public final static String TYPE = "relay";

    private static final long serialVersionUID = -2532723565735163573L;


    @Override
    public String getType() {
        return TYPE;
    }

    protected RelayedNotification(){

    }

    public RelayedNotification(final Subject subject,
                               final Observer observer,
                               final Account notifier) {
        super(subject, observer,notifier);
    }

    @Override
    public Notification getInstance(Observer observer) {
       return new RelayedNotification(getSubject(),observer,(Account)getNotifier());
   }
}
