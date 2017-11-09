<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<aside class="mui-off-canvas-left mui-transitioning mui-active" style="visibility: visible; z-index: 0;">
    <div class="mui-scroll-wrapper side-menu-scroll-wrapper" data-scroll="6">
        <div class="mui-scroll" style="transform: translate3d(0px, 0px, 0px) translateZ(0px);">
            <!-- 菜单具体展示内容 -->
            <!--个人信息部分-->
            <div class="person-info">
                <!--登录前-->
                <div class="un-login" hidden="">
                    <p>欢迎观临，请先登录</p>
                    <a type="button" class="mui-btn mui-btn-success  btn-login">用户登录</a>
                </div>
                <!--登陆后-->
                <div class="login">
                    <i class="icon-person"></i>
                    <p>EVEN_999</p>
                    <a type="button" class="mui-btn mui-btn-success  btn-person">个人中心</a>
                </div>
            </div>
            <!--side-nav-->
            <div class="side-nav">
                <ul class="mui-list-unstyled">
                    <li class="active"><a href="index.html">首页</a></li>
                    <li><a href="promo.html">优惠活动</a></li>
                    <li><a href="">下载客户端</a></li>
                    <li><a href="">账户存款</a></li>
                    <li><a href="">关于我们</a></li>
                    <li><a href="">常见问题</a></li>
                    <li><a href="">在线客服</a></li>
                    <li><a href="">注册条款</a></li>
                    <li class=" lang zh-CN">
                        <a href="">语言</a>
                    </li>
                </ul>
                <a type="button" class="mui-btn mui-btn-success  btn-logout">退出登录</a>
            </div>
        </div>
        <div class="mui-scrollbar mui-scrollbar-vertical"><div class="mui-scrollbar-indicator" style="transition-duration: 0ms; display: none; height: 663px; transform: translate3d(0px, 0px, 0px) translateZ(0px);"></div></div></div>
</aside>