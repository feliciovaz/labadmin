<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="css/ipadmin.css">
<title>Create IP address</title>
</head>
<body>
<div id="wrapper">
	<div class="ip-main">
		<h3>Add a new IP address</h3>
		<form action="ip" method="post">
			<table class="create-ip">
			<tbody>
			<tr>
				<th>IP Address:</th>
				<td>
					<input class="ip-byte" name="first-byte" type="number" name="ip-address" min="0" max="255" value="${firstByte}">.
					<input class="ip-byte" name="second-byte" type="number" name="ip-address" min="0" max="255" value="${secondByte}">.
					<input class="ip-byte" name="third-byte" type="number" name="ip-address" min="0" max="255" value="${thirdByte}">.
					<input class="ip-byte" name="fourth-byte" type="number" name="ip-address" min="0" max="255" value="${fourthByte}">			
				</td>
			</tr>
			<tr>
				<th>IP Description:</th>
				<td><input class="ip-description" type="text" name="ip-description" value="${ipDescription}"></td>
			<tr>
				<th>Allocated:</th>
				<td><input type="checkbox" name="ip-is-busy" value="Allocated" ${ipBusyChecked}></td>
			</tr>
			</tbody>
			</table>
		
			<input type="submit" name="ip-submit">
			<input type="button" onClick="location.href='ip'" value="Cancel">
		</form>
		
		<div id="status-messages">${status}</div>
	</div>
</div>
</body>
</html>