<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/ipadmin.css">
<title>Modify virtual machine</title>
</head>
<body>
<div id="wrapper">
	<div class="ip-operation">
		<form method="post" action="vm">
			<input type="hidden" name="operation" value="delete">
			<input type="hidden" name="vmName" value="${vm.name}">
			<input type="submit" value="Delete VM">
			<input type="button" value="Back to main" onClick="location.href='vm'">
		</form>
	</div>
	<div class="ip-main">
		<h3>Modify virtual machine</h3>
		<form action="vm" method="post">
			<input type="hidden" name="operation" value="modify">
			<table>
				<tbody>
					<tr>
						<td>Name:</td>
						<td><input type="text" name="vmName" value="${vm.name}" readonly></td>
					</tr>
					<tr>
						<td>Description:</td>
						<td><input type="text" name="vmDescription" value="${vm.description}"></td>
					</tr>
					<tr>
						<td>Operating System:</td>
						<td><input type="text" name="vmOS" value="${vm.os}"></td>
					</tr>
					<tr>
						<td>Owner:</td>
						<td><input type="text" name="vmOwner" value="${vm.owner}"></td>
					</tr>
				</tbody>
			</table>
			<input type="submit" value="Modify">
		</form>
	</div>
	<div class="ip-main">
		<h3>Roles of ${vm.name}</h3>
			
		<form action="vm" method="post">
					<input type="hidden" name="vmName" value="${vm.name}">
					<input type="hidden" name="operation" value="updrole">
		<table>
			<thead>
				<tr>
					<td><input type="text" name="vmRole" value=""></td>
					<td><input type="submit" name="add" value="add"></td>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${vmRoles}" var="vmRole">
				<tr>
				<td>${vmRole.role}</td>
				<td><a class="button-like" href="<c:url value="vm">
								<c:param name="operation" value="delrole"/>
								<c:param name="roleId" value="${vmRole.id}"/>
								<c:param name="vmName" value="${vm.name}"/>
							</c:url>" >del</a></td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
		</form>
	</div>
</div>
</body>
</html>