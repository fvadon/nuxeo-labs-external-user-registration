/**
 *
 */

package org.nuxeo.labs.users.we;


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
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.user.invite.UserInvitationComponent;
import org.nuxeo.ecm.user.invite.UserInvitationService;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;




/**
 * The root entry for the WebEngine module.
 * @author fvadon
 */
@Path("/register")
@Produces("text/html;charset=UTF-8")
@WebObject(type="ExternalRegistrationRoot")
public class ExternalRegistrationRoot extends ModuleRoot {

    public static final String SCHEMA_NAME = "userinfo";

    public static final String USERNAME_FIELD = "userinfo:login";

    public static final String PASSWORD_FIELD = "userinfo:password";

    public static final String FIRSTNAME_FIELD = "userinfo:firstName";

    public static final String LASTNAME_FIELD = "userinfo:lastName";

    public static final String EMAIL_FIELD = "userinfo:email";

    public static final String COMPANY_FIELD = "userinfo:company";

    public static final String GROUPS_FIELD = "userinfo:groups";
    public static final String TENANTID_FIELD = "userinfo:tenantId";
    public static final String CONFIGURATION_NAME = "external_user_registration";
    protected static Log log = LogFactory.getLog(UserInvitationService.class);

    protected String repoName = null;
    protected DocumentModelList searchResult;
    protected FormData data;

    @GET
    public Object doGet() {
        Map<String, String> data = new HashMap<String, String>();
        return getView("index").arg("data", data);
    }

    @POST
    @Path("company")
    @Produces("text/html")
    public Object submitForm() throws LoginException {

        data = getContext().getForm();
        String login = data.getString("login");
        String email = data.getString("email");
        String company = data.getString("company");


        if (login == null || "".equals(login.trim())) {
            return redisplayFormWithMessage("Cannot have empty login", data);
        }
        if (email == null || "".equals(email.trim())) {
            return redisplayFormWithMessage("Cannot have empty email", data);
        }

        if (company == null || "".equals(email.trim())) {
            return redisplayFormWithMessage("Cannot have empty company", data);
        }
        //to do here : check email format and existence of login/email (look at TrialObject class for idea)

        UnestrictedDomainSearcher domainSearcher = new UnestrictedDomainSearcher(data);
        domainSearcher.runUnrestricted();
        ctx.setProperty("searchResult", searchResult);

        Map<String, String> savedData = new HashMap<String, String>();
        for (String key : data.getKeys()) {
            savedData.put(key, data.getString(key));
        }

        return getView("company").arg("data", savedData);
    }

    @POST
    @Path("submit")
    @Produces("text/html")
    public Object submitCompanyForm() throws LoginException {

        data = getContext().getForm();
        String login = data.getString("login");
        String email = data.getString("email");
        String company = data.getString("company");


        if (login == null || "".equals(login.trim())) {
            return redisplayFormWithMessage("Cannot have empty login", data);
        }
        if (email == null || "".equals(email.trim())) {
            return redisplayFormWithMessage("Cannot have empty email", data);
        }

        if (company == null || "".equals(email.trim())) {
            return redisplayFormWithMessage("Cannot have empty company", data);
        }
        //to do here : check email format and existence of login/email (look at TrialObject class for idea)
        Map<String, Serializable> additionnalInfo = new HashMap<String, Serializable>();

        RegistrationSubmitor acceptor = new RegistrationSubmitor(data,additionnalInfo);
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

        protected FormData data;
        protected Map<String, Serializable> additionnalInfo;
        public RegistrationSubmitor(FormData data, Map<String, Serializable> additionnalInfo) {

            super(getTargetRepositoryName());
            this.data=data;
            this.additionnalInfo=additionnalInfo;

        }

        @Override
        public void run() throws ClientException {
            DocumentModel docUserInfo = session.createDocumentModel("UserInvitation");


            String title = "registration request for " + data.getString("login")
                    + " (" + data.getString("email") + " " + data.getString("company")
                    + ") ";

            @SuppressWarnings("deprecation")
            String name = IdUtils.generateId(title + "-"
                    + System.currentTimeMillis());

            UserInvitationService usrService = Framework.getLocalService(UserInvitationService.class);

            String targetPath = ((UserInvitationComponent) usrService).getOrCreateRootDocument(session,CONFIGURATION_NAME).getPathAsString();
            docUserInfo.setPathInfo(targetPath, name);
            docUserInfo.setPropertyValue("dc:title", title);

            // store userinfo
            docUserInfo.setPropertyValue(USERNAME_FIELD, data.getString("login"));
            //doc.setPropertyValue(UserRegistrationInfo.PASSWORD_FIELD,userInfo.getPassword());
            docUserInfo.setPropertyValue(FIRSTNAME_FIELD, data.getString("firstName"));
            docUserInfo.setPropertyValue(LASTNAME_FIELD,data.getString("lastName"));
            docUserInfo.setPropertyValue(EMAIL_FIELD, data.getString("email"));
            docUserInfo.setPropertyValue(COMPANY_FIELD,data.getString("company"));
            docUserInfo.setPropertyValue(TENANTID_FIELD,data.getString("tenantId"));


            usrService.submitRegistrationRequest(CONFIGURATION_NAME,docUserInfo, additionnalInfo,UserInvitationService.ValidationMethod.EMAIL , false);
        }
    }

    protected class UnestrictedDomainSearcher extends UnrestrictedSessionRunner {

        protected FormData data;
        public UnestrictedDomainSearcher(FormData data) {

            super(getTargetRepositoryName());
            this.data=data;

        }

        @Override
        public void run() throws ClientException {
            String query = "select * from Document where ecm:currentLifeCycleState != 'deleted' and ecm:primaryType='Domain'and ecm:fulltext='"
                    + data.getString("company") + "'";

            //DocumentModelList searchResult = null;
            try {
                searchResult = session.query(query);
            } catch (ClientException e) {
                log.error(e);
            }

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
