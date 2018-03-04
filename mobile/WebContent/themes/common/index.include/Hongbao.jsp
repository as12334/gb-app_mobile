<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<!--浮窗广告轮播-->
<c:if test="${not empty floatList}">
    <div class="ads-slider hongbao-slide-wrap hongbao-wrap" id="hongbao">
        <div class="icon-close"></div>
        <div class="mui-slider hongbao-slider">
            <div class="mui-slider-group">
                <c:forEach var="item" items="${floatList}">
                    <c:if test="${item.type=='moneyActivity'}">
                        <c:choose>
                            <c:when test="${item.cttFloatPic.singleMode}">
                                <div class="mui-slider-item hb_type_<c:choose><c:when test="${fn:contains(item.floatItem.normalEffect, 'panel-first')}">1</c:when><c:when test="${fn:contains(item.floatItem.normalEffect, 'panel-second')}">2</c:when><c:otherwise>3</c:otherwise></c:choose>">
                                    <div class="img"></div>
                                    <div class="extra float_idx" objectId="${item.activityId}"></div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="mui-slider-item">
                                    <img class="float_idx" objectId="${item.activityId}" data-src="${soulFn:getImagePath(domain,item.floatItem.normalEffect)}"
                                         src="${soulFn:getThumbPath(domain,item.floatItem.normalEffect,0,0)}">
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </c:forEach>

            </div>
        </div>
    </div>
</c:if>