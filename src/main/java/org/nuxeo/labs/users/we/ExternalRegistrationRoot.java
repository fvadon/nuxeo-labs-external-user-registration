/**
 *
 */

package org.nuxeo.labs.users.we;

import static org.nuxeo.ecm.user.invite.UserRegistrationInfo.EMAIL_FIELD;
import static org.nuxeo.ecm.user.invite.UserRegistrationInfo.USERNAME_FIELD;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.user.invite.UserInvitationComponent;
import org.nuxeo.ecm.user.invite.UserInvitationService;
import org.nuxeo.ecm.user.invite.UserRegistrationConfiguration;
import org.nuxeo.ecm.user.invite.UserRegistrationInfo;
//import org.nuxeo.ecm.user.registration.UserRegistrationService;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;


//import static org.nuxeo.ecm.user.invite.UserInvitationService.ValidationMethod.EMAIL;





/**
 * The root entry for the WebEngine module.
 * @author fvadon
 */
@Path("/register")
@Produces("text/html;charset=UTF-8")
@WebObject(type="ExternalRegistrationRoot")
public class ExternalRegistrationRoot extends ModuleRoot {

    private static final String USER_INFO = "userInfo";
    public static final String CONFIGURATION_NAME = UserRegistrationConfiguration.DEFAULT_CONFIGURATION_NAME;
    protected static Log log = LogFactory.getLog(UserInvitationService.class);

    protected String repoName = null;

    @GET
    public Object doGet() {
        Map<String, String> data = new HashMap<String, String>();
        return getView("index").arg("data", data);
    }

    @POST
    @Path("submit")
    @Produces("text/html")
    public Object submitForm() throws LoginException {

        FormData data = getContext().getForm();
        //String pw1 = data.getString("password");
        //String pw2 = data.getString("password_verif");
        String login = data.getString("login");
        String email = data.getString("email");
        //String country = data.getString("country");
        String company = data.getString("company");


        if (login == null || "".equals(login.trim())) {
            return redisplayFormWithMessage("Cannot have empty login", data);
        }
        if (email == null || "".equals(email.trim())) {
            return redisplayFormWithMessage("Cannot have empty email", data);
        }

        //to do here : check email format and existence of login/email.


        UserRegistrationInfo userInfo = new UserRegistrationInfo();

        userInfo.setLogin(login);
        userInfo.setFirstName(data.getString("firstName"));
        userInfo.setLastName(data.getString("lastName"));
        userInfo.setCompany(company);
        //userInfo.setPassword(pw1);
        userInfo.setEmail(email);
        //userInfo.setCountry(country);
      //to do here : check email format and existence of login/email (look at TrialObject class for idea)

       Map<String, Serializable> additionnalInfo = new HashMap<String, Serializable>();

       RegistrationSubmitor acceptor = new RegistrationSubmitor(userInfo,additionnalInfo);
       acceptor.runUnrestricted();

        return redirect(getPath() + "/success");


    }
    @GET
    @Path("success")
    @Produces("text/html")
    public Object viewSuccess() throws UnsupportedEncodingException {
        return getView("success");
    }

    protected Object redisplayFormWithMessage(String message, FormData data) {
        Map<String, String> savedData = new HashMap<String, String>();
        for (String key : data.getKeys()) {
            savedData.put(key, data.getString(key));
        }
        return getView("index").arg("data", savedData).arg("err",
                message);
    }


    protected class RegistrationSubmitor extends UnrestrictedSessionRunner {

        protected UserRegistrationInfo userInfo;
        protected Map<String, Serializable> additionnalInfo;
        public RegistrationSubmitor(UserRegistrationInfo userInfo, Map<String, Serializable> additionnalInfo) {

            super(getTargetRepositoryName());
            this.userInfo=userInfo;
            this.additionnalInfo=additionnalInfo;

        }

        @Override
        public void run() throws ClientException {
            DocumentModel docUserInfo = session.createDocumentModel("UserInvitation");


            String title = "registration request for " + userInfo.getLogin()
                    + " (" + userInfo.getEmail() + " " + userInfo.getCompany()
                    + ") ";

            @SuppressWarnings("deprecation")
            String name = IdUtils.generateId(title + "-"
                    + System.currentTimeMillis());

            UserInvitationService usrService = Framework.getLocalService(UserInvitationService.class);

            String targetPath = ((UserInvitationComponent) usrService).getOrCreateRootDocument(session,CONFIGURATION_NAME).getPathAsString();
            docUserInfo.setPathInfo(targetPath, name);
            docUserInfo.setPropertyValue("dc:title", title);

            // store userinfo
            docUserInfo.setPropertyValue(USERNAME_FIELD, userInfo.getLogin());
            //doc.setPropertyValue(UserRegistrationInfo.PASSWORD_FIELD,userInfo.getPassword());
            docUserInfo.setPropertyValue(UserRegistrationInfo.FIRSTNAME_FIELD, userInfo.getFirstName());
            docUserInfo.setPropertyValue(UserRegistrationInfo.LASTNAME_FIELD,userInfo.getLastName());
            docUserInfo.setPropertyValue(EMAIL_FIELD, userInfo.getEmail());
            docUserInfo.setPropertyValue(UserRegistrationInfo.COMPANY_FIELD,userInfo.getCompany());


            usrService.submitRegistrationRequest(docUserInfo, additionnalInfo,UserInvitationService.ValidationMethod.EMAIL , false);



        }
    }

    protected String getTargetRepositoryName() {

        if (repoName == null) {
            try {
                RepositoryManager rm = Framework.getService(RepositoryManager.class);
                repoName = rm.getDefaultRepository().getName();
            } catch (Exception e) {
                log.error("Error while getting default repository name", e);
                repoName = "default";
            }
        }
        return repoName;
    }


}
