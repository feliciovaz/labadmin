<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/ipadmin.css"> 
<title>Add a new Virtual Machine</title>
</head>
<body>
<div id="wrapper">
	<div class="ip-main">
		<h3>Add a new Virtual Machine address</h3>

		<form action="vm" method="post">
			<input type="hidden" name="operation" value="create">
			<table><tbody>
				<tr>
					<td>Name:</td>
					<td><input type="text" name="name" value="${name}"></td>
				</tr>
				<tr>
					<td>Description</td>
					<td><input type="text" name="description" value="${description}"></td>
				</tr>
				<tr>
					<td>Operating System</td>
					<td><input type="text" name="os" value="${os}"></td>
				</tr>
				<tr>
					<td>Owner</td>
					<td><input type="text" name="owner" value="${owner}"></td>
				</tr>			
			</tbody></table>
			<input type="submit" value="Add"><input type="button" value="Cancel" onClick="location.href='vm'">
		</form>

		<div id="status-messages">${status}</div>
	</div>
</div>
</body>
</html>