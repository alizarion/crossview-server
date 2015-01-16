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
@DiscriminatorValue(value = CrossNotification.TYPE)
public class CrossNotification extends Notification<Account> {

    public final static String TYPE ="cross";

    private static final long serialVersionUID = -7263757057068977269L;

    @Override
    public String getType() {
        return TYPE;
    }

    public CrossNotification() {
    }

    public CrossNotification(final Subject subject,
                             final Observer observer,
                             final Account notifier) {
        super(subject, observer,notifier);
    }

    @Override
    public Notification getInstance(Observer observer) {
        return new CrossNotification(getSubject(),observer,(Account)getNotifier());
    }
}
