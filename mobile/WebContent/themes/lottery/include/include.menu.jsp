<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- 菜单容器 -->
<aside id="offCanvasSideRight" class="mui-off-canvas-right">
    <div id="offCanvasSideScroll" class="mui-scroll-wrapper">
        <ul class="mui-table-view mui-table-view-chevron mui-table-view-inverted aside-right-menu">
            <li class="mui-table-view-cell mui-media">
                <a href="#">
                <c:choose>
                    <c:when test="${isLogin}">
                        <span class="mui-badge mui-badge-primary mui-icon mui-icon-refreshempty" id="refreshBalance"></span>
                        <img class="mui-media-object mui-pull-left" src="${soulFn:getThumbPathWithDefault(domain, sysUser.avatarUrl, 46, 46, resRoot.concat('/lottery/themes/images/member.png'))}">
                        <div class="mui-media-body">${sysUser.username}<p class="mui-ellipsis">${views.themes_auto['余额']}：<font class="col-red" id="_balance"></font>${views.themes_auto['元']}</p></div>
                    </c:when>
                    <c:otherwise>
                        <img class="mui-media-object mui-pull-left" src="${resRoot}/lottery/themes/images/member.png">
                        <a class="mui-btn mui-btn-success btn-login">${views.themes_auto['登录']}</a>
                    </c:otherwise>
                </c:choose>
                </a>
            </li>
            <li class="mui-table-view-cell">
                <a data-skip="/mainIndex.html" data-target="0" class="right-menu">
                    <span class="iconfont icon-home"></span>
                    <span>${views.themes_auto['首页']}</span>
                </a>
            </li>
            <li class="mui-table-view-cell">
                <a data-href="/lottery/mainIndex.html" class="right-menu">
                    <span class="iconfont icon-goucaidating"></span>
                    <span>${views.themes_auto['购彩大厅']}</span>
                </a>
            </li>
            <li class="mui-table-view-cell">
                <a data-href="/lottery/bet/betOrders.html" class="right-menu">
                    <span class="iconfont icon-touzhujilu"></span>
                    <span>${views.themes_auto['投注记录']}</span>
                </a>
            </li>
            <li class="mui-table-view-cell">
                <a data-href="/lottery/lotteryResultHistory/index.html" class="right-menu">
                    <span class="iconfont icon-kaijiangjilu"></span>
                    <span>${views.themes_auto['开奖记录']}</span>
                </a>
            </li>
            <li class="mui-table-view-cell">
                <a data-skip="/mine/index.html" data-target="4" class="right-menu">
                    <span class="iconfont icon-huiyuanzhongxin"></span>
                    <span>${views.themes_auto['会员中心']}</span>
                </a>
            </li>
            <li class="mui-table-view-cell">
                <a data-href="/message/gameNotice.html" class="right-menu">
                    <span class="iconfont icon-xinxizhongxin"></span>
                    <span>${views.themes_auto['消息中心']}</span>
                </a>
            </li>
            <li class="mui-table-view-cell">
                <a class="right-menu customer" data-target="3">
                    <span class="iconfont icon-zaixiankefu"></span>
                    <span>${views.themes_auto['在线客服']}</span>
                </a>
            </li>
        </ul>
        <c:if test="${isLogin}">
        <div class="p-1r">
            <button type="button" class="mui-btn mui-btn-danger mui-btn-block user-logout">${views.themes_auto['退出登录']}</button>
        </div>
        </c:if>
    </div>
</aside>
