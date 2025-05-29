<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Jsp/Servlet課題2</title>
<style>
<!-- フォームの外枠 -->
.bodyborder {
	border: 1px solid black;
	text-align: center;
	margin-top: 80px;
	margin-left: 500px;
	margin-right: 500px;
}
<!-- フォームのCSS -->
table {
	text-align: left;
	margin: auto;
}
</style>
</head>
<body>
	<form class="bodyborder" action="newProduct" method="post">
		<h3 style="padding-top: 20px;">商品登録画面</h3>
		<table style="padding-bottom: 50px;">
			<tr>
				<td>商品ID</td>
				<td><input type="text" value="${newId}" disabled /> <input
					type="hidden" name="productID" value="${newId}" /></td>
			</tr>
			<tr>
				<td>商品コード</td>
				<td><input type="text" name="productCode"
					value="${productCode}" /></td>
			</tr>
			<tr>
				<td>商品名</td>
				<td><input type="text" name="productName"
					value="${productName}" /></td>
			</tr>
			<tr>
				<td>価格</td>
				<td><input type="text" name="Price" value="${Price}" /></td>
			</tr>
			<tr>
				<td>カテゴリー</td>
				<td>
				<select id="category" name="category" value="">
						<!-- ドロップダウンリストをカテゴリーリスト文表示 -->
						<c:forEach var="list" items="${ctgList}">
							<!-- フォームが送信された後に、送信されたOPTIONを選択状態に -->
							<option value="${list.categoryID}"<c:if test="${list.categoryID == selectedCategoryID}">selected</c:if>>
								${list.categoryName}
							</option>
						</c:forEach>
				</select></td>
			</tr>
			<tr>
				<td></td>
				<td><input type="submit" value="登録" /></td>
			</tr>
		</table>
	</form>
	<!--　登録成功メッセージ -->
	<h1 style="text-align: center;">${succeed}</h1>
	<!-- エラーメッセージをエラーの分だけ表示 -->
	<c:forEach var="list" items="${errorMessage}">
		<h3 style="text-align: center; color: red;">ERROR : ${list}</h3>
	</c:forEach>
</body>
</html>