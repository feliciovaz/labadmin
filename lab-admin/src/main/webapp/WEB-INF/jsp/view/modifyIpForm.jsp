<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="css/ipadmin.css">
<title>Modify IP address ${ipAddress}</title>
</head>
<body>
<div id="wrapper">
	<div class="ip-operation">
		<form method="post" action="ip">
			<input type="hidden" name="operation" value="deleteIp">
			<input type="hidden" name="ipAddress" value="${ipAddress}">
			<input type="submit" value="Delete IP">
		</form>
	</div>
	<div class="ip-main">
		<h3>Modify IP address</h3>

		<form action="ip" method="post">
			<table class="create-ip">
			<tbody>
			<tr>
				<th>IP Address:</th>
				<td>
					<input class="ip-address" type="text" name="ipAddress" value="${ipAddress}" readonly>
				</td>
			</tr>
			<tr>
				<th>IP Description:</th>
				<td><input class="ip-description" type="text" name="ipDescription" value="${ipDescription}"></td>
			<tr>
				<th>Allocated:</th>
				<td><input type="checkbox" name="ipIsBeingUsed" value="Allocated" ${ipIsBeingUsedChecked}></td>
			</tr>
			</tbody>
			</table>

			<input type="hidden" name="operation" value="modifyIp">
			<input type="submit" value="Modify IP">
			<input type="button" onClick="location.href='ip'" value="Cancel">
		</form>

		<div id="status-messages">${status}</div>
	</div>
</div>
</body>
</html>