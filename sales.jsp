<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>JSP Servlet課題１</title>
</head>
<body>
	<table border="2">
		<tr>
			<th>従業員名</th>
			<th>売上金額</th>
		</tr>
		<!-- データの表を表示 -->
		<c:forEach var="list" items="${SalesList}">
			<tr>
				<td>${list.employeeName}</td>
				<td  style = "text-align:right;">${list.salesSum}</td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>