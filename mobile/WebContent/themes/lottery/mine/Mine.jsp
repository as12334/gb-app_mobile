<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>

<head>
    <title>${siteName}</title>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <script src="${resRoot}/js/plugin/inputNumber.js?v=${rcVersion}"></script>
    <%@ include file="/include/include.js.jsp" %>
</head>

<body class="gb-theme mine-page">
<!-- 侧滑导航根容器 -->
<div class="index-canvas mui-off-canvas-wrap mui-draggable">
    <!-- 菜单容器 -->
    <%@include file="../include/include.menu.jsp" %>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
            <!-- 主页面容器 -->
            <div class="mui-inner-wrap">
                <header class="mui-bar mui-bar-nav">
                    <a class="mui-icon mui-icon mui-pull-left icon-chat" data-url="/message/gameNotice.html"
                       id="unReadCount"></a>
                    <h1 class="mui-title">${views.mine_auto['我的']}</h1>
                    <a class="mui-action-menu index-action-menu mui-pull-right"></a>
                </header>
                <!--底部-->
                <c:if test="${os ne 'android'&&os ne 'app_ios'}">
                    <%@include file="/themes/lottery/include/include.footer.jsp" %>
                </c:if>
                <div class="mui-content mui-scroll-wrapper" id="mineRefreshContainer" >
                    <div class="mui-scroll">
                        <div class="mui-row">
                            <div class="gb-userinfo">
                                <div class="ct ${os}">
                                    <c:choose>
                                        <c:when test="${os eq 'android'}">
                                            <div class="setting"></div>
                                        </c:when>
                                        <c:otherwise>
                                            <a class="btn mui-btn mui-btn-outlined user-logout">${views.mine_auto['退出登录']}</a>
                                        </c:otherwise>
                                    </c:choose>
                                    <img src="${soulFn:getThumbPathWithDefault(domain, sysUser.avatarUrl,34,34, resRoot.concat('/images/avatar.png'))}"
                                         data-url="${root}/personalInfo/index.html" class="_sub avatar" data-demo="${isDemo}">
                                    <p>${soulFn:overlayString(sysUser.username)}</p>
                                    <p>
                                        <c:set var="_now" value="<%=new java.util.Date() %>" />
                                        <c:set value="${empty sysUser.loginTime? soulFn:formatDateTz(_now, DateFormat.DAY_SECOND, timeZone): views.mine_auto['本次登录时间'].concat(soulFn:formatDateTz(sessionSysUser.loginTime, DateFormat.DAY_SECOND, timeZone))}"
                                               var="loginTime"/>
                                        <small>${empty sysUser.lastLoginTime ? (empty loginTime ? lastActiveTime : loginTime):
                                                views.mine_auto['上次登录时间'].concat(soulFn:formatDateTz(sysUser.lastLoginTime, DateFormat.DAY_SECOND, timeZone))}
                                        </small>
                                    </p>
                                </div>
                                <p>
                                    <span class="span">
                                        <span class="color2">${views.mine_auto['钱包']}</span>
                                        <span class="color3" id="walletBalance"></span>
                                    </span>
                                    <span class="span">
                                        <span class="color2">${views.mine_auto['总资产']}</span>
                                        <span class="color3" id="totalAssets"></span>
                                    </span>
                                </p>
                                <div class="clearfix"></div>
                            </div>
                        </div>
                        <div class="mui-row">
                            <div class="gb-userlist m-t-sm">
                                <ul>
                                    <li>
                                        <a  class="item _app" data-skip="/wallet/deposit/index.html" data-target="1" data-os="${os}" data-demo="${isDemo}">
                                            <p><img src="${resRoot}/images/my-ico1.png" style="width: 33px;"></p>
                                            <div class="ct">
                                                <p>${views.mine_auto['存款']}</p>
                                            </div>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="" class="item" data-url="${isDemo ? '' : '/wallet/withdraw/index.html'}" data-demo="${isDemo}">
                                            <p><img src="${resRoot}/images/my-ico2.png" style="width: 28px;"></p>
                                            <div class="ct">
                                                <p>${views.mine_auto['取款']}</p>
                                                <p>
                                                    <small id="withdrawAmount"></small>
                                                </p>
                                            </div>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="" class="item" data-url="${root}/bankCard/page/addCard.html" data-demo="${isDemo}">
                                            <p><img src="${resRoot}/images/my-ico7.png" style="width: 31px;"></p>
                                            <div class="ct">
                                                <p>${views.mine_auto['银行卡']}</p>
                                                <p>
                                                    <small id="bankcardNumber">
                                                        <span id="bankImg"></span>
                                                    </small>
                                                </p>
                                            </div>
                                        </a>
                                    </li>
                                </ul>
                                <div class="clearfix"></div>
                            </div>
                        </div>
                        <div class="mui-row">
                            <div class="gb-userlist m-t-sm">
                                <ul>
                                    <li>
                                        <a href="" class="item" data-url="${root}/fund/record/index.html">
                                            <p><img src="${resRoot}/images/my-ico4.png" style="width: 25px;"></p>
                                            <div class="ct">
                                                <p>${views.mine_auto['资金记录']}</p>
                                            </div>
                                        </a>
                                    </li>
                                    <li>
                                        <a class="item" data-skip="${root}/lottery/bet/betOrders.html" data-target="3" data-os="${os}">
                                            <p><img src="${resRoot}/images/my-ico5.png" style="width: 31px;"></p>
                                            <div class="ct">
                                                <p>${views.mine_auto['投注记录']}</p>
                                                <p>
                                                    <small id="calSingleAmount"></small>
                                                </p>
                                            </div>
                                        </a>
                                    </li>
                                    <li>
                                        <a class="item" data-url="${root}/promo/myPromo.html">
                                            <p><img src="${resRoot}/images/my-ico6.png" style="width: 29px; margin-top: 4px;"></p>
                                            <div class="ct">
                                                <p>${views.mine_auto['优惠记录']}</p>
                                                <p>
                                                    <small id="preferentialAmount"></small>
                                                </p>
                                            </div>
                                        </a>
                                    </li>
                                </ul>
                                <div class="clearfix"></div>
                            </div>
                        </div>
                        <div class="mui-row">
                            <div class="gb-userlist m-t-sm">
                                <ul>
                                    <li>
                                        <a class="item" data-url="${root}/message/gameNotice.html?isSendMessage=true">
                                            <p><img src="${resRoot}/images/my-ico9.png"
                                                    style="width: 30px; margin-top: 4px;"></p>
                                            <div class="ct">
                                                <p>${views.mine_auto['申请优惠']}</p>
                                            </div>
                                        </a>
                                    </li>
                                    <li>
                                        <a class="item" data-url="${root}/my/password/editPassword.html" data-demo="${isDemo}">
                                            <p><img src="${resRoot}/images/my-ico10.png" style="width: 30px;"></p>
                                            <div class="ct">
                                                <p>${views.mine_auto['修改登录密码']}</p>
                                            </div>
                                        </a>
                                    </li>
                                    <li>
                                        <a class="item" data-url="${root}/passport/securityPassword/edit.html" data-demo="${isDemo}">
                                            <p><img src="${resRoot}/images/my-ico11.png" style="width: 26px;"></p>
                                            <div class="ct">
                                                <p>${views.mine_auto['修改安全密码']}</p>
                                            </div>
                                        </a>
                                    </li>
                                </ul>
                                <div class="clearfix"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="mui-off-canvas-backdrop"></div>
            </div>
        </div>
    </div>
</body>

<script>
    curl(['site/my/Mine', 'site/passport/password/PopSecurityPassword', 'site/common/Menu', 'site/common/Footer', 'site/common/DynamicSeparation'],
            function (Page, Security, Menu, Footer, Dynamic) {
                page = new Page();
                page.security = new Security();
                page.menu = new Menu();
                page.menu = new Footer();
                page.dynamic = new Dynamic();
            });
</script>

</html>
