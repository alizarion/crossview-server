package com.alizarion.crossview.mbean;

import com.alizarion.reference.exception.ApplicationError;
import com.alizarion.reference.resource.exception.ConfigurationError;
import com.alizarion.reference.resource.exception.PersistentResourceNotFoundException;
import com.alizarion.reference.resource.mbean.PersistentMBean;
import com.alizarion.reference.security.doa.CredentialJpaDao;
import com.alizarion.reference.security.entities.Role;
import com.alizarion.reference.security.exception.RoleNotFoundException;

import javax.ejb.Stateless;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Managed file params, root folder, temp folder .
 * @author selim@openlinux.fr.
 */
@Stateless
public class CrossViewConfigMBean extends PersistentMBean {

    private static final long serialVersionUID = -8298079648347720667L;


    public final static String CATEGORY =
            "com.alizarion.crossview.service.properties";

    public final static String MANAGED_FILE_ROOT_FOLDER =
            "managed-file-root-folder";

    public final static String COMMA_SEPARATE_INIT_ROLES =
            "comma-separate-init-roles";

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    public URI getManagedFileRootFolder() throws
            PersistentResourceNotFoundException {
        try {
            return getValueAsURI(MANAGED_FILE_ROOT_FOLDER);
        } catch (URISyntaxException e) {
            try {
                return new URI(ResourceBundle.getBundle("app.config").
                        getString(MANAGED_FILE_ROOT_FOLDER));
            } catch (URISyntaxException e1) {
                throw new ApplicationError("bad URI Syntax to get " +
                        MANAGED_FILE_ROOT_FOLDER + "property",e);
            }
        }
    }

    public Set<Role>  geInitRoles() {
        CredentialJpaDao roleDAO =  new CredentialJpaDao(this.em);
        String[] tabRoles ;
        try {
            tabRoles = getValue(COMMA_SEPARATE_INIT_ROLES).split(",");


        } catch (PersistentResourceNotFoundException e) {
            tabRoles =  ResourceBundle
                    .getBundle("app.config")
                    .getString(COMMA_SEPARATE_INIT_ROLES)
                    .split(",");
        }

        Set<Role> roles =  new HashSet<>();
        for(String rolekey : tabRoles){
            try {
                roles.add(roleDAO.getRoleByKey(rolekey));
            } catch (RoleNotFoundException e) {
                throw new ConfigurationError("cannot found configured role");
            }
        }
        return roles;
    }
}
