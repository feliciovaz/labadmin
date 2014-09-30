<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--@elvariable id="ipAddresses" type="java.util.List<pt.alu.lab.IpAddress>"--%>
<%--@elvariable id="ipAddress" type="pt.alu.lab.IpAddress"--%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="css/ipadmin.css">
<title>Lab list of IP addresses</title>
</head>
<body>
<div class="ip-operation">
	<form action="ip" method="get">
		<input type="hidden" name="operation" value="showCreateIpForm">
		<input type="submit" value="Add IP">
	</form>
</div>
<div class="ip-main">
	<h3>List of IP addresses</h3>
	<table class="imagetable">
		<thead>
		<tr>
			<th>IP address</th>
			<th>Description</th>
			<th>Status</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${ipAddresses}" var="ipAddress">
		<tr class="${ipAddress.getPresentationClassForList()}">
			<td>
				<a class="row-like" href="<c:url value="ip">
							<c:param name="operation" value="showModifyIpForm"/> 
							<c:param name="ipAddress" value="${ipAddress.ip}"/> 
							<c:param name="ipDescription" value="${ipAddress.description}"/>
							<c:param name="ipIsBeingUsed" value="${ipAddress.isBeingUsed.toString()}"/>
						</c:url>">${ipAddress.ip}</a>
			</td>
			<td>
				<a class="row-like" href="<c:url value="ip">
							<c:param name="operation" value="showModifyIpForm"/> 
							<c:param name="ipAddress" value="${ipAddress.ip}"/> 
							<c:param name="ipDescription" value="${ipAddress.description}"/>
							<c:param name="ipIsBeingUsed" value="${ipAddress.isBeingUsed.toString()}"/>
						</c:url>">${ipAddress.description}</a>
			</td>
			<td>
				<c:choose>
				<c:when test="${ipAddress.isBeingUsed}">Used</c:when>
				<c:otherwise>Free</c:otherwise>
				</c:choose>
			</td>
		</tr>
		</c:forEach>
		</tbody>
	</table>
</div>
</body>
</html>