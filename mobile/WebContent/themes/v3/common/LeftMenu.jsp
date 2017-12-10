<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<aside class="mui-off-canvas-left mui-transitioning mui-active" style="visibility: visible; z-index: 0;">
    <div class="mui-scroll-wrapper side-menu-scroll-wrapper">
        <div class="mui-scroll">
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
                    <i class="icon-person">
                        <img width="100%" height="100%" src="${resRoot}/images/avatar.png" id="avatarImg">
                    </i>
                    <p></p>
                    <soul:button target="goTab" skip="4" dataHref="/mine/index.html?channel=mine&skip=4" isLeft="true" text="个人中心" opType="function" cssClass="mui-btn mui-btn-success btn-person" tag="button"/>
                </div>
            </div>
            <!--side-nav-->
            <div class="side-nav">
                <ul class="mui-list-unstyled">
                    <%--<li class="active home"><soul:button target="${root}/mainIndex.html" text="首页" opType="href"/></li>--%>
                    <%--<li class="pro"><soul:button target="${root}/discounts/index.html?skip=1" text="优惠活动" opType="href" cssClass=""/></li>--%>
                    <li class="home ${empty skip && empty path?'active':''}"><soul:button target="goTab" isLeft="true" skip="2" text="首页" dataHref="/mainIndex.html" opType="function"/></li>
                    <li class="pro ${skip == 1?'active':''}"><soul:button target="goTab" text="优惠活动" isLeft="true" skip="1" dataHref="/discounts/index.html?skip=1" opType="function" cssClass=""/></li>
                    <li class="download"><soul:button target="${root}/downLoad/downLoad.html" text="下载客户端" opType="href"/><%-- <a href="">下载客户端</a>--%></li>
                    <li class="pc"><soul:button target="goPC" opType="function" text="电脑版"/> <%--<a href="">电脑版</a>--%></li>
                    <li class="trans"> <soul:button target="${root}/transfer/index.html" text="转账" opType="href"/></li>
                    <li class="deposit"><soul:button target="goTab" skip="0" isLeft="true" dataHref="/wallet/deposit/index.html" text="账户存款" opType="function"/></li>
                    <li class="about ${path == 'about'?'active':''}"><soul:button target="${root}/mainIndex.html?path=about" text="关于我们" opType="href"/><%--<a href="">关于我们</a>--%></li>
                    <li class="question"><soul:button target="${root}/help/firstType.html" text="常见问题" opType="href"/><%--<a href="">常见问题</a>--%></li>
                    <li class="service"><soul:button target="goTab" isLeft="true" text="在线客服" skip="3" dataHref="" opType="function"/></li>
                    <li class="reg_rules ${path == 'terms'?'active':''}"><soul:button target="${root}/mainIndex.html?path=terms" text="注册条款" opType="href"/><%--<a href="">注册条款</a>--%></li>
                    <li class="lang ${fn:replace(language, '_', '-')}">
                        <soul:button target="lang" text="语言" opType="function"/>
                    </li>
                </ul>
                <div class="login">
                    <soul:button target="logout" text="退出登录" opType="function" cssClass="mui-btn mui-btn-success btn-logout"></soul:button>
                </div>
            </div>
        </div>
    </div>
</aside>
<%@include file="LangMenu.jsp"%>