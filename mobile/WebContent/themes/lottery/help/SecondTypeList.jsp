<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<html>

<head>
	<%@ include file="/include/include.head.jsp" %>
	<title>${name}</title>
	<%@ include file="/include/include.js.jsp" %>
</head>

<body class="gb-theme help-center">
<div class="mui-draggable mui-off-canvas-wrap">
	<div class="mui-inner-wrap">
		<c:if test="${os ne 'android'}">
			<header class="mui-bar mui-bar-nav">
				<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
				<h1 class="mui-title">${name}</h1>
			</header>
		</c:if>
		<div class="mui-content mui-scroll-wrapper" style="${os eq 'android'?'padding-top:0;':''}">
			<div class="">
				<div class="mui-scroll">
					<ul class="mui-table-view">
						<c:forEach items="${command}" var="helpMenu">
							<li class="mui-table-view-cell">
								<a data-href="/help/detail.html?searchId=${VHelpTypeAndDocumentListVo.getSearchId(helpMenu.get('id'))}">${helpMenu.get('name')}</a>
							</li>
						</c:forEach>
					</ul></div>
			</div>

		</div>
	</div>
</div>

</body>
<script>
	curl(['site/help/Help'], function (Help) {
		help = new Help();
	})
</script>
</html>
