<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!--红包html开始  通过改变hongbao-wrap的class来改变红包样式，一共三种（hb_type_1,hb_type_2,hb_type_3）-->
<div class="hongbao-slide-wrap hongbao-wrap" id="hongbao">
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
        <div class="mui-slider-indicator">
            <div class="mui-indicator mui-active"></div>
            <div class="mui-indicator"></div>
            <div class="mui-indicator"></div>
        </div>
    </div>
</div>
<div id="hongbao_detail" class="hongbao_detail">
    <div class="hongbao_inner">
        <div class="icon-close"></div>
        <div class="hongbao"><!--未能拆时加disabled类名-->
            <div class="icon-open"></div>
            <div class="hongbao-time-txt">下次拆红包开始时间为</div>
            <div class="hongbao-time">2017-11-11 11:11:11</div>
            <a href="javascript:" class="btn-rule" id="btn-rule"></a>
            <!--红包规则元素-->
            <div class="hongbao-rule">
                <div class="txt">
                    <div class="nice-wrapper">
                        游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则
                    </div>
                </div>
                <a href="javascript:" class="icon-close-rule"></a>
            </div>
            <!--中奖时的提示-->
            <div class="win-hongbao tips">
                <div class="ttxt-1">恭喜您</div>
                <div class="ttxt-2">获得20元</div>
            </div>
            <!--未中奖时的提示-->
            <div class="lose-hongbao tips">
                <div class="ttxt-1">很遗憾</div>
                <div class="ttxt-2">还差一点就中奖了呦！</div>
            </div>
        </div>
        <div class="hongbao_extra"></div>
        <!--拆开红包时的彩带和光环-->
        <div class="caidai"></div>
        <div class="hongbao-light"></div>
        <!--关闭红包继续抽奖按钮-->
        <a href="javascript:" id="btn-ok" class="btn-ok"></a>
    </div>
</div>