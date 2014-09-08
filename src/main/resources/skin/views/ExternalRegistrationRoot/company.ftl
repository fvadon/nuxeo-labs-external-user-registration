<@extends src="base.ftl">
<@block name="header">List of possible matching companies </@block>

<@block name="content">

<div>
	<h4>Choose a company</h4>
	<p>If you recognise your existing company in the system, you can select it, otherwise, you can create a new one. By default, you will be the Administrator of this company.
	  <form method="POST" action="${This.path}/submit" enctype="application/x-www-form-urlencoded">
<#list Context.getProperty("searchResult") as company>
	<input type="radio" name="tenantId" value=${company.name}>${company.title}<br>
</#list>

	<input type="radio" name="tenantId" value="000-NewCompany">New Company<br>
	Here are the information you already submitted <br>
		  Login: <input type="text" name="login" value="${data['login']}" style="background-color:#F0F0F0" readonly/> <br/>
		  E-mail: <input type="text" name="email" value="${data['email']}" style="background-color:#F0F0F0" readonly/> <br/>
		  First name: <input type="text" name="firstName" value="${data['firstName']}" style="background-color:#F0F0F0" readonly/> <br/>
		  lastName: <input type="text" name="lastName" value="${data['lastName']}" style="background-color:#F0F0F0" readonly/> <br/>
		  Company: <input type="text" name="company" value="${data['company']}" style="background-color:#F0F0F0"readonly/> <br/>
		  Submit:   <input type="submit" value="Submit Company choice"/>

	</form>
	
</form>

</@block>
</@extends>
