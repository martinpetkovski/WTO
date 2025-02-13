<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf8">

	<title>Login</title>
	<link rel="icon" type="image/ico" href="${pageContext.request.contextPath}/resources/favicon.ico">
	<link href="${pageContext.request.contextPath}/resources/style/main.css" rel="stylesheet">
	<link href="${pageContext.request.contextPath}/resources/style/index.css" rel="stylesheet">
	<link href="${pageContext.request.contextPath}/resources/style/login.css" rel="stylesheet">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/script/main.js" type="text/javascript"></script>
</head>

<body>
	<!-- HEADER START -->
		<jsp:include page="includes/header.jsp" />
	<!-- HEADER END -->
	<!-- MAIN CONTENT START -->


	<div class="mainContent">
		<div class="mainContentWrapper">
			<div class="dropMenu" id="login">
				<div class="dropMenuWrapper">
					<img src="${pageContext.request.contextPath}/resources/style/regular-show.png">
					<div class="title">
						Login
					</div>
					<form name='loginForm' action="<c:url value='/login' />" method='POST'>
						<input id="username" placeholder="username" name="username" type="text" autocomplete="off"><br>
						<input placeholder="password" name="password" type="password" id="last"><button class="button" id="submit">-&gt;</button>
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		            </form>
				</div>
			</div>
		</div>
	</div>
	<!-- MAIN CONTENT END -->
	<!-- FOOTER START -->


	<footer>
	</footer>
	<!-- FOOTER END -->
</body>
</html>