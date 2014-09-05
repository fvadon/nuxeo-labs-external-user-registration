<@extends src="base.ftl">
<@block name="header">Welcome new potential user</@block>

<@block name="content">

<!-- <div style="margin: 10px 10px 10px 10px">
<p>
This is the view corresponding to your root object: ${This.class.simpleName}.
</p>

<p>
You can find the code of this view in: src/main/resources/skin/views/${This.class.simpleName}
</p>

<p>
To render a view from an WebEngine object you should create @GET annotated method which is returning the view: getView("viewname") where <i>viewname</i> is the file name (without the ftl extension) in the views/ObjectName folder.   
</p>

<p>
In a view you can access the object instance owning the view using ${r"${This}"} variable or the request context using the ${r"${Context}"} variable.  
</p>

<p>
Also, you can use @block statements to create reusable layouts.
</p>

</div> -->

  <#if err??>
    <div class="errorFeedback">
    Error: 
	${err}<br/>
    </div>
  </#if>

<div>
  <form method="POST" action="${This.path}/submit" enctype="application/x-www-form-urlencoded">
	  Login: <input type="text" name="login" value="${data['login']}" required/> <br/>
	  E-mail: <input type="text" name="email" value="${data['email']}" required/> <br/>
	  First name: <input type="text" name="firstName" value="${data['firstName']}"/> <br/>
	  lastName: <input type="text" name="lastName" value="${data['lastName']}"/> <br/>
	  Company: <input type="text" name="company" value="${data['company']}"/> <br/>
	  Submit:   <input type="submit" value="Submit"/>
</div>
</form>





</@block>
</@extends>
