<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>

<body class="gb-theme help-center">
<div class="mui-draggable mui-off-canvas-wrap">
    <div class="mui-inner-wrap">
        <c:if test="${os ne 'android'}">
            <header class="mui-bar mui-bar-nav">
                <%--<%@ include file="/include/include.toolbar.jsp" %>--%>
                    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left "></a>
                <h1 class="mui-title">${name}</h1>
            </header>
        </c:if>
        <div class="mui-content mui-scroll-wrapper" style="${os eq 'android'?'padding-top:0!important;':''}">
            <div class="mui-scroll">
                <dl class="fqa mui-collapse">
                    <c:forEach var="document" items="${command}">
                        <dt class="mui-control-item" data-rel='{"target":"chooseQuestion","opType":"function"}'>
                            <span>${document.helpTitle}</span>
                        </dt>
                        <dd class="mui-content">
                                ${document.helpContent.replaceAll("&lt;", "<").replaceAll("&gt;", ">")}
                        </dd>
                    </c:forEach>
                </dl>
            </div>
        </div>
    </div>
</div>
</body>
<%@ include file="../../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/help/Help.js"></script>
