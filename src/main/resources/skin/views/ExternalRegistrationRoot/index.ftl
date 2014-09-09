<@extends src="base.ftl">

<@block name="content">


  <#if err??>
    <div class="errorFeedback">
    Error: 
	${err}<br/>
    </div>
  </#if>


<div class="row">
  <div class="col-md-12"><p>Please give us a few information to validate your request. You will be able to choose a password once your request has been accepted.</p></div>
</div>

<form role="form" method="POST" action="${This.path}/company" enctype="application/x-www-form-urlencoded">
  <div class="form-group">
    <label for="InputLogin">Login</label>
    <input type="text" class="form-control" id="InputLogin" placeholder="login" name="login" value="${data['login']}" required/>
  </div>
  <div class="form-group">
    <label for="InputEmail">Email address</label>
    <input type="email" class="form-control" id="InputEmail" placeholder="foobar@example.com" name="email" value="${data['email']}"required/>
  </div>
  <div class="form-group">
    <label for="InputCompany">Company</label>
    <input type="text" class="form-control" id="InputCompany" placeholder="Company" name="company" value="${data['company']}" required/>
  </div>
  <div class="form-group">
    <label for="InputFirstName">First Name</label>
    <input type="text" class="form-control" id="InputFirstName" placeholder="First Name" name="firstName" value="${data['firstName']}"/>
  </div>  
  <div class="form-group">
    <label for="InputLastName">Last Name</label>
    <input type="text" class="form-control" id="InputLastName" placeholder="Last Name" name="lastName" value="${data['lastName']}"/>
  </div>
  <button type="submit" class="btn btn-default" value="Submit">Submit</button>
</form>






</@block>
</@extends>
