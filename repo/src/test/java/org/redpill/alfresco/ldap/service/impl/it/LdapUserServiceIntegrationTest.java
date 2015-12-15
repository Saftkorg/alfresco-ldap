package org.redpill.alfresco.ldap.service.impl.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.redpill.alfresco.ldap.it.AbstractLdapRepoIntegrationTest;
import org.redpill.alfresco.ldap.service.LdapUserService;
import org.redpill.alfresco.ldap.service.impl.LdapUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class LdapUserServiceIntegrationTest extends AbstractLdapRepoIntegrationTest {

  @Autowired
  @Qualifier("rl.ldapUserService")
  protected LdapUserService ldapUserService;

  private static final String DEFAULT_PASSWORD = "springfield";
  private static final String CHANGED_PASSWORD = "springfield2";
  private static final String USER_LISA = "lisa";
  private static final String USER_ABRAHAM = "abraham";
  private static final String USER_MONA = "mona";
  private static final String USER_TUT = "tut";
  private static final String USER_HAPPY = "happy";

  @Test
  public void testChangePasswordAsUser() {
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());
    ldapUserService.changePassword(USER_LISA, DEFAULT_PASSWORD, CHANGED_PASSWORD);
    _authenticationService.authenticate(USER_LISA, CHANGED_PASSWORD.toCharArray());
    assertEquals(USER_LISA, AuthenticationUtil.getFullyAuthenticatedUser());
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    ldapUserService.changePassword(USER_LISA, CHANGED_PASSWORD, DEFAULT_PASSWORD);
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());
  }

  @Test
  public void testChangePasswordAsSystem() {
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());
    ldapUserService.changePassword(USER_LISA, null, CHANGED_PASSWORD);
    _authenticationService.authenticate(USER_LISA, CHANGED_PASSWORD.toCharArray());
    assertEquals(USER_LISA, AuthenticationUtil.getFullyAuthenticatedUser());
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    ldapUserService.changePassword(USER_LISA, null, DEFAULT_PASSWORD);
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());
  }

  @Test
  public void testPasswordAlgorithms() {
    // Change to SHA
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());
    ((LdapUserServiceImpl) ldapUserService).setPasswordAlgorithm("sha");
    ldapUserService.changePassword(USER_LISA, null, CHANGED_PASSWORD);
    _authenticationService.authenticate(USER_LISA, CHANGED_PASSWORD.toCharArray());
    assertEquals(USER_LISA, AuthenticationUtil.getFullyAuthenticatedUser());
    // Reset
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    ldapUserService.changePassword(USER_LISA, null, DEFAULT_PASSWORD);
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());

    // Change to SSHA
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());
    ((LdapUserServiceImpl) ldapUserService).setPasswordAlgorithm("ssha");
    ldapUserService.changePassword(USER_LISA, null, CHANGED_PASSWORD);
    _authenticationService.authenticate(USER_LISA, CHANGED_PASSWORD.toCharArray());
    assertEquals(USER_LISA, AuthenticationUtil.getFullyAuthenticatedUser());
    // Reset
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    ldapUserService.changePassword(USER_LISA, null, DEFAULT_PASSWORD);
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());

    // Change to MD5
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());
    ((LdapUserServiceImpl) ldapUserService).setPasswordAlgorithm("md5");
    ldapUserService.changePassword(USER_LISA, null, CHANGED_PASSWORD);
    _authenticationService.authenticate(USER_LISA, CHANGED_PASSWORD.toCharArray());
    assertEquals(USER_LISA, AuthenticationUtil.getFullyAuthenticatedUser());
    // Reset
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    ldapUserService.changePassword(USER_LISA, null, DEFAULT_PASSWORD);
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());

    // Change to unsupported
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());
    ((LdapUserServiceImpl) ldapUserService).setPasswordAlgorithm("unsupported");
    try {
      ldapUserService.changePassword(USER_LISA, null, CHANGED_PASSWORD);
      assertTrue("Shoudl receive exception", false);
    } catch (AlfrescoRuntimeException e) {

    }
    
    //Reset to default
    ((LdapUserServiceImpl) ldapUserService).setPasswordAlgorithm("ssha");
  }

  @Test
  public void testSystemAddUser() {
    _testAddUserAsSystem(USER_ABRAHAM);
  }

  protected void _testAddUserAsSystem(String user) {
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());
    ldapUserService.createUser(user, DEFAULT_PASSWORD, user + "@simpson.com", StringUtils.capitalize(user), "Simpson");
    _authenticationService.authenticate(user, DEFAULT_PASSWORD.toCharArray());
    assertEquals(user, AuthenticationUtil.getFullyAuthenticatedUser());
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());
  }

  @Test
  public void testDeleteUserAsSystem() {
    _testAddUserAsSystem(USER_MONA);
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());
    ldapUserService.deleteUser(USER_MONA);
    try {
      _authenticationService.authenticate(USER_MONA, DEFAULT_PASSWORD.toCharArray());
      assertTrue(false);
    } catch (AuthenticationException e) {
      // Expected
    }
  }

  @Test
  public void testUserEditAsSystem() {
    _testAddUserAsSystem(USER_TUT);
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());
    ldapUserService.editUser(USER_TUT, null, null, null, "Tut2", "Simpson2");
  }

  @Test
  public void testUserEditAsUser() {
    _testAddUserAsSystem(USER_HAPPY);
    _authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());
    assertEquals(AuthenticationUtil.getSystemUserName(), AuthenticationUtil.getFullyAuthenticatedUser());
    ldapUserService.editUser(USER_HAPPY, DEFAULT_PASSWORD, CHANGED_PASSWORD, "happy2@simpson.com", null, null);
  }

}
