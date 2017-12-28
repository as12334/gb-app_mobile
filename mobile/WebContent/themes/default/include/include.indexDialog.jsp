<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--消息弹窗-->
<c:if test="${not empty phoneDialog}">
    <div id="middlePopover" class="mui-popover notice-popover-wrap">
        <div class="mui-popup mui-popup-in">
            <div class="mui-popup-inner">
                <c:forEach var="d" items="${phoneDialog}" varStatus="vs">
                    <div class="mui-popup-title">${d.name}</div>
                    <div class="cont-text">
                        <c:choose>
                            <c:when test="${d.content_type == 1}">
                                <img src="${soulFn:getThumbPath(domain, d.cover,128,128)}"/>
                            </c:when>
                            <c:otherwise>
                                ${d.content}
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <c:set var="link" value="${not empty d.link ? d.link:''}"/>
                    <c:if test="${not empty link}">
                        <c:set var="link" value="${fn:startsWith(link, 'http://')||fn:startsWith(link, 'https://')?link:'http://'.concat(link)}"/>
                    </c:if>
                    <soul:button text="" target="dialog" dataLink="${link}" opType="function" cssClass="mui-btn mui-btn-block confirm-btn">确定</soul:button>
                </c:forEach>
            </div>
        </div>
    </div>
</c:if>