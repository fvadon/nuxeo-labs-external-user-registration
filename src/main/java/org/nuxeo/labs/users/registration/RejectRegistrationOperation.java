/**
 *
 */

package org.nuxeo.labs.users.registration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.core.Events;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.util.BaseURL;
import org.nuxeo.ecm.user.invite.UserInvitationService;
import org.nuxeo.runtime.api.Framework;

/**
 * @author fvadon
 */
@Operation(id=RejectRegistrationOperation.ID, category=Constants.CAT_DOCUMENT, label="Reject Registration Operation",
description="Reject a registration request given as input")
public class RejectRegistrationOperation {

    public static final String ID = "RejectRegistrationOperation";
    public static final String CONFIGURATION_NAME="external_user_registration";
    public static final String REQUESTS_DOCUMENT_LIST_CHANGED = "requestDocumentsChanged";


    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel input) {
        UserInvitationService userInvitationService= Framework.getService(UserInvitationService.class);

        Map<String, Serializable> additionalInfo = new HashMap<String, Serializable>();
        additionalInfo.put("validationBaseURL", BaseURL.getBaseURL()
                + userInvitationService.getConfiguration(CONFIGURATION_NAME).getValidationRelUrl());
        userInvitationService.rejectRegistrationRequest(input.getId(),
                additionalInfo);
        // EventManager.raiseEventsOnDocumentChange(request);
        Events.instance().raiseEvent(REQUESTS_DOCUMENT_LIST_CHANGED);


        return input;
    }

}
