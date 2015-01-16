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
@DiscriminatorValue(value = FollowNotification.TYPE)
public class FollowNotification extends Notification<Account> {

    public final static String TYPE = "follow";

    private static final long serialVersionUID = -1344321961980872613L;


    public FollowNotification(final Subject subject,
                              final Observer observer,
                              final Account notifier) {
        super(subject, observer,notifier);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    protected FollowNotification(){

    }

    @Override
    public Notification getInstance(Observer observer) {
        return new FollowNotification(getSubject(),observer,(Account)getNotifier());
    }
}
