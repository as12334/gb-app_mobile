<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<aside class="mui-off-canvas-left mui-transitioning mui-active" style="visibility: visible; z-index: 0;">
    <div class="mui-scroll-wrapper side-menu-scroll-wrapper" data-scroll="6">
        <div class="mui-scroll" style="transform: translate3d(0px, 0px, 0px) translateZ(0px);">
            <!-- 菜单具体展示内容 -->
            <!--个人信息部分-->
            <div class="person-info">
                <!--登录前-->
                <div class="un-login">
                    <p>欢迎观临，请先登录</p>
                    <soul:button target="login" text="用户登录" opType="function" cssClass="mui-btn mui-btn-success btn-login" tag="button"/>
                </div>
                <!--登陆后-->
                <div class="login" style="display: none;">
                    <i class="icon-person"></i>
                    <p></p>
                    <soul:button target="${root}/memberCentre/index.html?skip=4" text="个人中心" opType="href" cssClass="mui-btn mui-btn-success btn-person" tag="button"/>
                </div>
            </div>
            <!--side-nav-->
            <div class="side-nav">
                <ul class="mui-list-unstyled">
                    <li>
                        <soul:button target="${root}/mainIndex.html" text="首页" opType="href"/>
                    </li>
                    <li>
                        <soul:button target="${root}/wallet/deposit/index.html" text="账户存款" opType="href"/>
                    </li>
                    <li>
                        <soul:button target="${root}/transfer/index.html" text="额度转换" opType="href"/>
                    </li>
                    <li><a href="promo.html">优惠活动</a></li>
                    <li>
                        <soul:button target="${root}/downLoad/downLoad.html" text="" opType="href">下载客户端</soul:button>
                    </li>
                    <li><a href="">关于我们</a></li>
                    <li><a href="">常见问题</a></li>
                    <li><a href="">在线客服</a></li>
                    <li><a href="">注册条款</a></li>
                    <li class="lang ${fn:replace(language, '_', '-')}">
                        <a href="">语言</a>
                    </li>
                </ul>
                <div class="login">
                    <soul:button target="loginOut" text="退出登录" opType="function" cssClass="mui-btn mui-btn-success btn-logout"></soul:button>
                </div>
            </div>
        </div>
        <div class="mui-scrollbar mui-scrollbar-vertical"><div class="mui-scrollbar-indicator" style="transition-duration: 0ms; display: none; height: 663px; transform: translate3d(0px, 0px, 0px) translateZ(0px);"></div></div></div>
</aside>