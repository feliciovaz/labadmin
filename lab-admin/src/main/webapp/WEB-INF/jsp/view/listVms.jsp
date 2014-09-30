<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="css/ipadmin.css">
<title>Lab list of Virtual Machines</title>
</head>
<body>
	<div class="ip-operation">
		<form action="vm" method="get">
			<input type="hidden" name="operation" value="create">
			<input type="submit" value="Add VM">
			<input type="button" value="Back to main" onClick="location.href='/lab-admin'">
		</form>
	</div>
	<div class="ip-main">
		<h3>List of Virtual Machines</h3>
		<table class="imagetable">
			<thead>
			<tr>
				<th>Name</th>
				<th>Description</th>
				<th>OS</th>
				<th>Owner</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${virtualMachines}" var="virtualMachine">
				<tr>
					<td>
						<a class="row-like" href="<c:url value="vm">
									<c:param name="operation" value="modify"/> 
									<c:param name="vmName" value="${virtualMachine.name}"/> 
								</c:url>">${virtualMachine.name}</a>
					</td>
					<td>
						<a class="row-like" href="<c:url value="vm">
									<c:param name="operation" value="modify"/> 
									<c:param name="vmName" value="${virtualMachine.name}"/> 
								</c:url>">${virtualMachine.description}</a>
					</td>
					<td>
						<c:out value="${virtualMachine.os}" />
					</td>
					<td>
						<c:out value="${virtualMachine.owner}" />
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>