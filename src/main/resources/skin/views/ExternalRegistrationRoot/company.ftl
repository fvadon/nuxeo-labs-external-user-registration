<@extends src="base.ftl">
<@block name="header">
		<div class="page-header">
			<h2>Company Selection</small></h2>
		</div>
</@block>

<@block name="content">

	<div class="row">
	  <div class="col-md-12"><p>Here is the list of existing companies in the system that meet your requirements, you can select one of them or choose to ask for the creation of a new company (disabled for now)</p>
	  </div>
	</div>
	
<form role="form" method="POST" action="${This.path}/submit" enctype="application/x-www-form-urlencoded">
  
  
  <#list Context.getProperty("searchResult") as company>
	<div class="radio">
	  <label>
	    <input type="radio" name="tenantId" id="${company.name}" required value="${company.name}">
		${company.title}
	  </label>
	</div>
  </#list>
  
<div class="radio disabled">
  <label>
    <input type="radio" name="tenantId" id="000-NewCompany" required value="000-NewCompany" disabled>
Request the creation of a new company
  </label>
</div>

  <div class="col-md-12"><p>Here are the information you already provided, if you would like to change them, please use the back button of your Internet Browser</p>
  </div>
  
  <div class="form-group">
    <label for="InputLogin">Login</label>
    <input type="text disabled" class="form-control" id="InputLogin" placeholder="login" name="login" value="${data['login']}" readonly/>
  </div>
  <div class="form-group">
    <label for="InputEmail">Email address</label>
    <input type="email disabled" class="form-control" id="InputEmail" placeholder="foobar@example.com" name="email" value="${data['email']}"readonly/>
  </div>
  <div class="form-group">
    <label for="InputCompany">Company</label>
    <input type="text disabled" class="form-control" id="InputCompany" placeholder="Company" name="company" value="${data['company']}" readonly/>
  </div>
  <div class="form-group">
    <label for="InputFirstName">First Name</label>
    <input type="text disabled" class="form-control" id="InputFirstName" placeholder="First Name" name="firstName" value="${data['firstName']}" readonly/>
  </div>  
  <div class="form-group">
    <label for="InputLastName">Last Name</label>
    <input type="text disabled" class="form-control" id="InputLastName" placeholder="Last Name" name="lastName" value="${data['lastName']}" readonly/>
  </div>
  <button type="submit" class="btn btn-default" value="Submit Company choice">Submit Company choice</button>
</form>







</@block>
</@extends>
