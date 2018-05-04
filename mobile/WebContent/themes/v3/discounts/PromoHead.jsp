<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<header class="mui-bar mui-bar-nav">
    <a data-rel='{"target":"leftMenu","opType":"function"}' class="mui-icon mui-action-menu mui-icon-bars mui-pull-left"></a>
    <div id="notLogin">
        <%--<soul:button target="" text="${views.themes_auto['试玩']}" opType="href" cssClass="mui-btn mui-btn-success mui-pull-right btn-demo"/>--%>
        <a data-rel='{"target":"${root}/signUp/index.html","opType":"href"}' class="mui-btn mui-btn-success mui-pull-right btn-login">${views.themes_auto['注册']}</a>
        <a data-rel='{"target":"${root}/login/commonLogin.html","opType":"href"}' class="mui-btn mui-btn-success mui-pull-right btn-login">${views.themes_auto['登录']}</a>
    </div>
    <%@include file="../common/Assert.jsp"%>
    <img src="${appLogo}" alt="logo" class="logo">
    <h1 class="mui-title">优惠活动</h1>
    <div class="promo-header">
        <div class="swiper-container p-t-slide-indicators promo-sorts">
            <div class="swiper-wrapper">
                <a class="swiper-slide mui-btn btn-promo-sort" data-rel='{"target":"activityType","opType":"function","activityType":""}'><span>${views.themes_auto['全部']}</span></a>
                <c:forEach var="type" items="${messageVo.typeList}" varStatus="vs">
                    <c:if test="${fn:length(messageVo.typePlayMessageMap.get(type.key)) > 0}">
                        <a class="swiper-slide mui-btn btn-promo-sort" name="${type.key}" data-rel='{"target":"activityType","opType":"function","activityType":"${type.key}"}'><span>${type.value}</span></a>
                    </c:if>
                </c:forEach>
            </div>
        </div>
    </div>
    <!--易记域名-->
    <%--<section class="yjym">${views.themes_auto['易记域名']}：${domain}</section>--%>
</header>
