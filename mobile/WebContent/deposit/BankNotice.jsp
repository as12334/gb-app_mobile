<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!--公告-->
<input type="hidden" value="${bankNotices.result.size()}" id="bankNoticeCount">
<button type="button" class="mui-pull-left mui-btn mui-btn-danger mui-btn-outlined">${views.deposit_auto['公告']}</button>
<div class="mui-list-unstyled gb-notice-list">
    <div class="m-outer">
        <div id="marquee-box" class="m-box">
            <c:forEach items="${bankNotices.result}" var="a" varStatus="vs">
                <a data-idx="${vs.index}" style="color:#000;margin-right:160px">${a.content.replaceAll('\\n','')}</a>
            </c:forEach>
        </div>
    </div>
</div>

<div class="masker masker-notice-box"></div>
<div class="gb-notice-box">
    <div class="hd"><p>${views.deposit_auto['公告']}</p></div>
    <div class="ct">
        <div class="" id="box-notice">
            <div class="mui-slider-group" style="margin-bottom: 10px">
                <c:choose>
                    <c:when test="${fn:length(bankNotices.result)>0}">
                        <c:forEach items="${bankNotices.result}" var="a" varStatus="vs">
                            <div class="mui-slider-item">
                                <p>${a.content}</p>
                            </div>
                        </c:forEach>
                    </c:when>
                </c:choose>
            </div>

            <c:if test="${fn:length(bankNotices.result) > 1}">
                <div class="mui-slider-indicator">
                    <c:forEach items="${bankNotices.result}" var="carousel" begin="0" end="${fn:length(bankNotices.result)}" varStatus="vs">
                        <div class="mui-indicator gray ${vs.index==0?'mui-active':''}"></div>
                    </c:forEach>
                </div>
            </c:if>
        </div>
    </div>
</div>

<%--</c:if>--%>