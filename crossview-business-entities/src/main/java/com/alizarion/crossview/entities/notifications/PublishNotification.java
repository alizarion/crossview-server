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
@DiscriminatorValue(value = PublishNotification.TYPE)
public class PublishNotification extends Notification<Account> {

    public static final String TYPE= "PUBLISH";
    private static final long serialVersionUID = 7102423739317436363L;

    @Override
    public String getType() {
        return TYPE;
    }

    public PublishNotification(){

    }

    public PublishNotification(Subject subject, Observer observer, Account notifier) {
        super(subject, observer, notifier);
    }

    @Override
    public Notification getInstance(Observer observer) {
         return new PublishNotification(getSubject(),observer,(Account)getNotifier());
    }
}
