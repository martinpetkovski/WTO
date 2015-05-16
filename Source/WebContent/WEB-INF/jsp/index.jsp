<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf8">

	<title>Front Page</title>
	<link rel="icon" type="image/ico" href="${pageContext.request.contextPath}/resources/favicon.ico">
	<link href="${pageContext.request.contextPath}/resources/style/main.css" rel="stylesheet">
	<link href="${pageContext.request.contextPath}/resources/style/index.css" rel="stylesheet">
	<script src=
	"https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/script/main.js" type="text/javascript"></script>
	<script src="${pageContext.request.contextPath}/resources/script/index.js" type="text/javascript"></script>
</head>

<body>
	<!-- HEADER START -->
		<jsp:include page="includes/header.jsp" />
	<!-- HEADER END -->
	<!-- MAIN CONTENT START -->


	<div class="mainContent">
		<div class="sort">
			<a href="${pageContext.request.contextPath}/feed"><div class="sortItem">news feed</div></a>
			<a href="${pageContext.request.contextPath}/bypoints"><div class="sortItem">by highest scoring</div></a>
			<a href="${pageContext.request.contextPath}"><div class="sortItem">by newest first</div></a>
			<a href="${pageContext.request.contextPath}/byrandom"><div class="sortItem">by random number</div></a>
		</div>
		<div class="mainContentWrapper">
			<c:forEach var="Image" items="${Images }">
				<a href="${pageContext.request.contextPath}/image/${Image.getAddress()}">
					<div class="image">
						<div class="title">
							<span>${Image.getTitle() }</span>
						</div>
						<img src="${Image.getContent() }">
					</div>
				</a>
			</c:forEach>
		</div>
		<div class="pages">
			<c:forEach begin="${PagesStart}" end="${PagesEnd}" varStatus="loop">
				<a href="${pageContext.request.contextPath}/${loop.index}"><div class="page" <c:if test="${Page == loop.index}"> id="active" </c:if>>${loop.index}</div></a>
			</c:forEach>
		</div>
	</div>
	<!-- MAIN CONTENT END -->
	<!-- FOOTER START -->


	<footer>
	</footer>
	<!-- FOOTER END -->
</body>
</html>