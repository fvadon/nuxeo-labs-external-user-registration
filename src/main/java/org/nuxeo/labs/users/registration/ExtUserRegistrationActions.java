/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * Contributors:
 * Nuxeo - initial API and implementation
 */

package org.nuxeo.labs.users.registration;

import static org.jboss.seam.international.StatusMessage.Severity.ERROR;
import static org.jboss.seam.international.StatusMessage.Severity.INFO;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.util.BaseURL;
import org.nuxeo.ecm.user.invite.UserInvitationService;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

@Name("extUserRegistrationActions")
@Scope(ScopeType.CONVERSATION)
public class ExtUserRegistrationActions implements Serializable {

    private static final long serialVersionUID = 534681164827894L;

    private static Log log = LogFactory.getLog(ExtUserRegistrationActions.class);

    //public static final String MULTIPLE_EMAILS_SEPARATOR = ";";

    public static final String REQUEST_DOCUMENT_LIST = "CURRENT_USER_REQUESTS";

    public static final String REQUESTS_DOCUMENT_LIST_CHANGED = "requestDocumentsChanged";
    public static final String CONFIGURATION_NAME="external_user_registration";

    //protected UserRegistrationInfo userinfo = new UserRegistrationInfo();

    //protected DocumentRegistrationInfo docinfo = new DocumentRegistrationInfo();

    //protected String multipleEmails;

    protected String comment;

    protected boolean copyOwner = false;

    @In(create = true)
    protected transient NavigationContext navigationContext;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true)
    protected transient DocumentsListsManager documentsListsManager;

    @In(create = true)
    protected transient ContentViewActions contentViewActions;

    @In(create = true)
    protected transient UserInvitationService userInvitationService;

    public String getValidationBaseUrl(String name) throws ClientException {
        return BaseURL.getBaseURL()
                + userInvitationService.getConfiguration(name).getValidationRelUrl();
    }

    public String getEnterPasswordUrl(String name) throws ClientException {
        return BaseURL.getBaseURL()
                + userInvitationService.getConfiguration(name).getEnterPasswordUrl();
    }

    public String getInvitationLayout(String name) {
        return userInvitationService.getConfiguration(name).getInvitationLayout();
    }

    public String getListingLocalContentView(String name) {
        return userInvitationService.getConfiguration(name).getListingLocalContentView();
    }

    public String getValidationBaseUrl() throws ClientException {
        return getValidationBaseUrl(CONFIGURATION_NAME);
    }

    public String getEnterPasswordUrl() throws ClientException {
        return getEnterPasswordUrl(CONFIGURATION_NAME);
    }

    // Accept the request made by external user.
    public void acceptRegistrationRequest(DocumentModel request) {
        try {
            Map<String, Serializable> additionalInfo = new HashMap<String, Serializable>();
            additionalInfo.put("enterPasswordUrl", getEnterPasswordUrl());
            // Determine the document url to add it into the email
            /*String docId = (String) request.getPropertyValue(DocumentRegistrationInfo.DOCUMENT_ID_FIELD);
            DocumentRef docRef = new IdRef(docId);
            DocumentModel doc = documentManager.getDocument(docRef);
            String docUrl = DocumentModelFunctions.documentUrl(doc);
            additionalInfo.put("docUrl", docUrl);*/

            userInvitationService.acceptRegistrationRequest(request.getId(),
                    additionalInfo);

            // EventManager.raiseEventsOnDocumentChange(request);
            Events.instance().raiseEvent(REQUESTS_DOCUMENT_LIST_CHANGED);
        } catch (ClientException e) {
            facesMessages.add(ERROR, e.getMessage());
        }
    }

    public void rejectRegistrationRequest(DocumentModel request) {
        try {
            Map<String, Serializable> additionalInfo = new HashMap<String, Serializable>();
            additionalInfo.put("validationBaseURL", getValidationBaseUrl());
            userInvitationService.rejectRegistrationRequest(request.getId(),
                    additionalInfo);
            // EventManager.raiseEventsOnDocumentChange(request);
            Events.instance().raiseEvent(REQUESTS_DOCUMENT_LIST_CHANGED);
        } catch (ClientException e) {
            facesMessages.add(ERROR, e.getMessage());
        }
    }

    public boolean getCanValidate() {
        boolean canDelete = !documentsListsManager.isWorkingListEmpty(REQUEST_DOCUMENT_LIST);
        for (DocumentModel doc : documentsListsManager.getWorkingList(REQUEST_DOCUMENT_LIST)) {
            canDelete &= isDocumentValidable(doc);
        }
        return canDelete;
    }

    protected boolean isDocumentValidable(DocumentModel doc) {
        try {
            return "accepted".equals(doc.getCurrentLifeCycleState());
        } catch (ClientException e) {
            log.warn("Unable to get lifecycle state for " + doc.getId() + ": "
                    + e.getMessage());
            log.debug(e);
            return false;
        }
    }

    public boolean getCanDelete() {
        boolean canDelete = !documentsListsManager.isWorkingListEmpty(REQUEST_DOCUMENT_LIST);
        for (DocumentModel doc : documentsListsManager.getWorkingList(REQUEST_DOCUMENT_LIST)) {
            canDelete &= isDocumentDeletable(doc);
        }
        return canDelete;
    }

    protected boolean isDocumentDeletable(DocumentModel doc) {
        try {
            return !"validated".equals(doc.getCurrentLifeCycleState());
        } catch (ClientException e) {
            log.warn("Unable to get lifecycle state for " + doc.getId() + ": "
                    + e.getMessage());
            log.debug(e);
            return false;
        }
    }


    public void validateUserRegistration() {
        if (!documentsListsManager.isWorkingListEmpty(REQUEST_DOCUMENT_LIST)) {
            try {
                for (DocumentModel registration : documentsListsManager.getWorkingList(REQUEST_DOCUMENT_LIST)) {
                    userInvitationService.validateRegistration(
                            registration.getId(),
                            new HashMap<String, Serializable>());
                }
                Events.instance().raiseEvent(REQUESTS_DOCUMENT_LIST_CHANGED);
                facesMessages.add(
                        INFO,
                        resourcesAccessor.getMessages().get(
                                "label.validate.request"));
                documentsListsManager.resetWorkingList(REQUEST_DOCUMENT_LIST);
            } catch (ClientException e) {
                log.warn("Unable to validate registration: " + e.getMessage());
                log.info(e);
                facesMessages.add(
                        ERROR,
                        resourcesAccessor.getMessages().get(
                                "label.unable.validate.request"));
            }
        }
    }

    public void deleteUserRegistration() {
        if (!documentsListsManager.isWorkingListEmpty(REQUEST_DOCUMENT_LIST)) {
            try {
                userInvitationService.deleteRegistrationRequests(
                        documentManager,
                        documentsListsManager.getWorkingList(REQUEST_DOCUMENT_LIST));
                Events.instance().raiseEvent(REQUESTS_DOCUMENT_LIST_CHANGED);
                facesMessages.add(
                        INFO,
                        resourcesAccessor.getMessages().get(
                                "label.delete.request"));
                documentsListsManager.resetWorkingList(REQUEST_DOCUMENT_LIST);
            } catch (ClientException e) {
                log.warn("Unable to delete user request:" + e.getMessage());
                log.info(e);
                facesMessages.add(
                        ERROR,
                        resourcesAccessor.getMessages().get(
                                "label.unable.delete.request"));
            }

        }
    }

    protected Map<String, Serializable> getAdditionalsParameters() {
        Map<String, Serializable> additionalsInfo = new HashMap<String, Serializable>();
        try {
            additionalsInfo.put("docinfo:documentTitle",
                    navigationContext.getCurrentDocument().getTitle());
            if (copyOwner) {
                additionalsInfo.put(
                        "registration:copyTo",
                        ((NuxeoPrincipal) documentManager.getPrincipal()).getEmail());
            }
            additionalsInfo.put("registration:comment", comment);
        } catch (ClientException e) {
            // log it silently as it will break anything
            log.debug(e, e);
        }
        return additionalsInfo;
    }

    @Observer({ REQUESTS_DOCUMENT_LIST_CHANGED })
    public void refreshContentViewCache() {
        contentViewActions.refreshOnSeamEvent(REQUESTS_DOCUMENT_LIST_CHANGED);
        contentViewActions.resetPageProviderOnSeamEvent(REQUESTS_DOCUMENT_LIST_CHANGED);
    }
}
