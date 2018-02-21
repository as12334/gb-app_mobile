<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>

<body class="gb-theme help-center">
<div class="mui-draggable mui-off-canvas-wrap">
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav">
            <a data-rel='{"target":"goToLastPage","opType":"function"}' class="mui-icon mui-icon-left-nav mui-pull-left "></a>
            <h1 class="mui-title">${views.help_auto['帮助中心']}</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper">
            <div class="mui-scroll">
                <ul class="mui-table-view">
                    <c:forEach items="${command}" var="helpMenu">
                        <li class="mui-table-view-cell">
                            <a class="mui-navigate-right" id="helpList" data-rel='{"target":"/help/secondType.html?searchId=${VHelpTypeAndDocumentListVo.getSearchId(helpMenu.get('id'))}","opType":"href"}'>
                                    ${helpMenu.get('name')}</a>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </div>
</div>
</body>
<%@ include file="../../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/help/Help.js"></script>

