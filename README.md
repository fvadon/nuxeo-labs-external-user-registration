Nuxeo External User Registration
===================

This project creates a webengine page to allow external user to join Nuxeo. It also add the corresponding link on the front page.

## Implementation Description

The project uses the service provided by Nuxeo Invite [https://github.com/nuxeo/nuxeo-services/tree/master/nuxeo-invite](https://github.com/nuxeo/nuxeo-services/tree/master/nuxeo-invite) and he inspired by Nuxeo-Registration for the logic ([https://github.com/nuxeo/nuxeo-platform-user-registration](https://github.com/nuxeo/nuxeo-platform-user-registration).

The landing class is ExternalRegistrationRoot.java that is called by the web engine page. It creates a user registration request that can then be seen by the administrator in the Admin Center (no notification is sent to Admin when a new user registration is created).

2 actions bean are defined to accept and reject user registration requests from the UI and are used on the content view to display them in the admin center. There are also 2 automation operations available in case you would like to create a workflow to review a request.

*Note: the project is not unit tested and requires an smtp server to be set up on the server to work*

## Build

You can build the plugin with `mvn clean install`

## User Doc

When installed, the plugin add a link on the home page, it redirects to a registration page where a potential user can provide and submit information.

When submitted a user registration request can be seen by an Administrator in the Admin center (Self External User Registration tab).

Requests can be accepted or rejected from here, they can be edited to select a group for instance. (the summary layout does not enable to accept/reject the request directly).

When a request is accepted, an email is sent to the user so that she/he can choose a password and then access the system (if a group has been defined).

*Please note that very full control are made, on the email for instance.*

## About Nuxeo

Nuxeo provides a modular, extensible Java-based [open source software platform for enterprise content management](http://www.nuxeo.com/en/products/ep) and packaged applications for [document management](http://www.nuxeo.com/en/products/document-management), [digital asset management](http://www.nuxeo.com/en/products/dam) and [case management](http://www.nuxeo.com/en/products/case-management). Designed by developers for developers, the Nuxeo platform offers a modern architecture, a powerful plug-in model and extensive packaging capabilities for building content applications.

More information on: <http://www.nuxeo.com/>
