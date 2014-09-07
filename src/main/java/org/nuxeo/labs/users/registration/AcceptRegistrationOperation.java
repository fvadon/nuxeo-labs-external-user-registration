/**
 *
 */

package org.nuxeo.labs.users.registration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.core.Events;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.util.BaseURL;
import org.nuxeo.ecm.user.invite.UserInvitationService;
import org.nuxeo.runtime.api.Framework;

/**
 * @author fvadon
 */
@Operation(id=AcceptRegistrationOperation.ID, category=Constants.CAT_DOCUMENT, label="Accept Registration",
description="This operation will accept a UserInvivation doc type and accept it.")
public class AcceptRegistrationOperation {

    public static final String ID = "AcceptRegistrationOperation";
    public static final String CONFIGURATION_NAME="external_user_registration";
    public static final String REQUESTS_DOCUMENT_LIST_CHANGED = "requestDocumentsChanged";

    @Context
    protected CoreSession session;

    @OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel input) {

        UserInvitationService userInvitationService= Framework.getService(UserInvitationService.class);

        Map<String, Serializable> additionalInfo = new HashMap<String, Serializable>();
        additionalInfo.put("enterPasswordUrl", BaseURL.getBaseURL()
                + userInvitationService.getConfiguration(CONFIGURATION_NAME).getEnterPasswordUrl());
        userInvitationService.acceptRegistrationRequest(input.getId(),
                additionalInfo);

        // EventManager.raiseEventsOnDocumentChange(request);
        Events.instance().raiseEvent(REQUESTS_DOCUMENT_LIST_CHANGED);
        //input=session.saveDocument(input);
        return input;
    }

}
