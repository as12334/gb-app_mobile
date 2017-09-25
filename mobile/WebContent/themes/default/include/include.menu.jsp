<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- 菜单容器 -->
<aside class="mui-off-canvas-left canvas-wrapper">
    <div class="mui-scroll-wrapper">
        <div class="mui-scroll">
            <div class="c-l-header">
                <span class="icon-personal">
                    <input type="hidden" value="${resRoot}" id="resRoot">
                    <img src="${soulFn:getThumbPathWithDefault(domain, sysUser.avatarUrl, 36, 36, resRoot.concat('/images/avatar.png'))}"
                         width="100%" height="100%" alt="" id="avatarImg">
                </span>
                <!-- 登录状态 -->
                <div class="login-status mui-hide _leftLogin">
                    <p class="h-text _leftUsername">${soulFn:overlayString(sysUser.username)}</p>
                    <a data-skip="/mine/index.html" class="h-btn" data-target="4">${views.include_auto['个人中心']}</a>
                </div>
                <!-- 未登录状态 -->
                <div class="unlogin-status mui-hide _leftUnLogin">
                    <p class="h-text">${views.include_auto['欢迎光临']}</p>
                    <a class="h-btn btn-login">${views.include_auto['用户登录']}</a>
                </div>
            </div>
            <div class="c-nav">
                <ul class="mui-table-view menu">
                    <li class="mui-table-view-cell ${channel eq 'index'?'active':''}" data-skip="/" data-target="0" data-os="${os}">
                        <a><span class="icon-canvas ic-index"></span>${views.include_auto['首页2']}</a>
                    </li>
                    <li class="mui-table-view-cell _download _downloadApp mui-hide" data-download="/app/download.html">
                        <a><span class="icon-canvas ic-download"></span>${views.include_auto['客户端']}</a>
                    </li>
                    <li class="mui-table-view-cell" data-skip="/wallet/deposit/index.html" data-target="1" data-os="${os}">
                        <a><span class="icon-canvas ic-deposit"></span>${views.include_auto['账户存款']}</a>
                    </li>
                    <li class="mui-table-view-cell _app" data-skip="/wallet/withdraw/index.html" data-target="2" data-os="${os}"><a><span
                            class="icon-canvas ic-change"></span>${views.include_auto['额度转换']}</a>
                    </li>
                    <li class="mui-table-view-cell" data-href="/help/firstType.html">
                        <a><span class="icon-canvas ic-problem"></span>${views.include_auto['帮助中心']}</a>
                    </li>
                    <li class="mui-table-view-cell customer" data-target="3" data-os="${os}">
                        <a><span class="icon-canvas ic-online"></span>${views.include_auto['在线客服']}</a>
                    </li>
                </ul>
            </div>
            <div class="c-bottom mui-hide _leftLogout">
                <a href="#" class="btn-logout user-logout">${views.include_auto['退出登录']}</a>
            </div>
        </div>
    </div>
</aside>
