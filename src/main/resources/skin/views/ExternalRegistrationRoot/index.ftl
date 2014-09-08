<@extends src="base.ftl">
<@block name="header">Welcome new potential user</@block>

<@block name="content">


  <#if err??>
    <div class="errorFeedback">
    Error: 
	${err}<br/>
    </div>
  </#if>

<div>
<p> Please give us a few information to validate your request. You will be able to choose a password once your request has been accepted.</p> </br>
  <form method="POST" action="${This.path}/company" enctype="application/x-www-form-urlencoded">
	  Login: <input type="text" name="login" value="${data['login']}" required/> <br/>
	  E-mail: <input type="text" name="email" value="${data['email']}" required/> <br/>
	  First name: <input type="text" name="firstName" value="${data['firstName']}"/> <br/>
	  lastName: <input type="text" name="lastName" value="${data['lastName']}"/> <br/>
	  Company: <input type="text" name="company" value="${data['company']}"required/> <br/>
	  Submit:   <input type="submit" value="Submit"/>

</form>
</div>




</@block>
</@extends>
