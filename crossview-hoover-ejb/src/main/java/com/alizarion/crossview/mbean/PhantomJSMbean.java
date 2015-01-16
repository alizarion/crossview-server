package com.alizarion.crossview.mbean;

import com.alizarion.reference.resource.exception.PersistentResourceNotFoundException;
import com.alizarion.reference.resource.mbean.PersistentMBean;

import javax.ejb.Stateless;
import java.util.ResourceBundle;

/**
 * @author selim@openlinux.fr.
 */
@Stateless
public class PhantomJSMbean extends PersistentMBean {

    private static final long serialVersionUID = 7851822564078334658L;

    private static final String CATEGORY =
            "com.alizarion.crossview.services.phantomjs";

    private final static String PATH_TO_PHANTOM_EXECUTABLE =
            "path-to-executable";

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    public  String getPathToPhantomExecutable() {
        try {
            return getValue(PATH_TO_PHANTOM_EXECUTABLE);
        } catch (PersistentResourceNotFoundException e) {
            return ResourceBundle.getBundle("app.config").getString("path-to-executable");
        }
    }
}
