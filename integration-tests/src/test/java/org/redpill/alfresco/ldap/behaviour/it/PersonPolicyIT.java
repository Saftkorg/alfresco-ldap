package org.redpill.alfresco.ldap.behaviour.it;

import static org.junit.Assert.*;

import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.util.PropertyMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.redpill.alfresco.ldap.it.AbstractLdapRepoIT;

public class PersonPolicyIT extends AbstractLdapRepoIT {

    @Before
    public void setUp() {
        authenticationComponent.setCurrentUser(AuthenticationUtil.getAdminUserName());
    }

    @After
    public void afterClassSetup() {
        authenticationComponent.clearCurrentSecurityContext();
    }

    @Ignore
    @Test
    public void testCreateAlfrescoPerson() {
        String USER_HOWLAND = "howland" + System.currentTimeMillis();
        // NodeRef userNodeRef = createUser(USER_HOWLAND);
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
            @Override
            public NodeRef execute() throws Throwable {

                PropertyMap properties = new PropertyMap(3);
                properties.put(ContentModel.PROP_USERNAME, USER_HOWLAND);
                properties.put(ContentModel.PROP_FIRSTNAME, "Howland");
                properties.put(ContentModel.PROP_LASTNAME, "Simpson");
                properties.put(ContentModel.PROP_EMAIL, "testmail@.malinator.com");
                properties.put(ContentModel.PROP_PASSWORD, "testpassword");

                NodeRef userNodeRef = personService.createPerson(properties);
                assertNotNull(userNodeRef);
                return null;
            }
        }, false, isRequiresNew());

        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>() {
            @Override
            public NodeRef execute() throws Throwable {
        Set<String> authorityZones = authorityService.getAuthorityZones(USER_HOWLAND);
        boolean inLdapAuthorityZone = false;
        for (String authorityZone : authorityZones) {
            if (authorityZone.startsWith(AuthorityService.ZONE_AUTH_EXT_PREFIX)) {
                inLdapAuthorityZone = true;
            }
        }
        assertTrue(inLdapAuthorityZone);
        return null;
            }
        }, false, isRequiresNew());
    }
}
