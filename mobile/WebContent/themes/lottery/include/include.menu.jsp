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
                        <div class="mui-media-body">${sysUser.username}<p class="mui-ellipsis">余额：<font class="col-red" id="_balance"></font>元</p></div>
                    </c:when>
                    <c:otherwise>
                        <img class="mui-media-object mui-pull-left" src="${resRoot}/lottery/themes/images/member.png">
                        <a class="mui-btn mui-btn-success btn-login">登录</a>
                    </c:otherwise>
                </c:choose>
                </a>
            </li>
            <li class="mui-table-view-cell">
                <a data-href="/mainIndex.html" class="right-menu">
                    <span class="iconfont icon-home"></span>
                    <span>首页</span>
                </a>
            </li>
            <li class="mui-table-view-cell">
                <a data-href="/lottery/mainIndex.html" class="right-menu">
                    <span class="iconfont icon-goucaidating"></span>
                    <span>购彩大厅</span>
                </a>
            </li>
            <li class="mui-table-view-cell">
                <a data-href="/lottery/bet/betOrders.html" class="right-menu">
                    <span class="iconfont icon-touzhujilu"></span>
                    <span>投注记录</span>
                </a>
            </li>
            <li class="mui-table-view-cell">
                <a data-href="/lottery/lotteryResultHistory/index.html" class="right-menu">
                    <span class="iconfont icon-kaijiangjilu"></span>
                    <span>开奖记录</span>
                </a>
            </li>
            <li class="mui-table-view-cell">
                <a data-href="/mine/index.html" class="right-menu">
                    <span class="iconfont icon-huiyuanzhongxin"></span>
                    <span>会员中心</span>
                </a>
            </li>
            <li class="mui-table-view-cell">
                <a data-href="/message/gameNotice.html" class="right-menu">
                    <span class="iconfont icon-xinxizhongxin"></span>
                    <span>消息中心</span>
                </a>
            </li>
            <li class="mui-table-view-cell">
                <a class="right-menu customer">
                    <span class="iconfont icon-zaixiankefu"></span>
                    <span>在线客服</span>
                </a>
            </li>
        </ul>
        <div class="p-1r">
            <button type="button" class="mui-btn mui-btn-danger mui-btn-block user-logout">退出登录</button>
        </div>
    </div>
</aside>
