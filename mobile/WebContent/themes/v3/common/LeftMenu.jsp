<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<aside id="offCanvasSide" class="mui-off-canvas-left mui-transitioning">
    <div id="offCanvasSideScroll" class="mui-scroll-wrapper side-menu-scroll-wrapper">
        <div class="mui-scroll">
            <!-- 菜单具体展示内容 -->
            <!--个人信息部分-->
            <div class="person-info">
                <!--登录前-->
                <div class="un-login">
                    <p>${views.themes_auto['欢迎观临']}</p>
                    <soul:button target="login" text="${views.themes_auto['用户登录']}" opType="function" cssClass="mui-btn mui-btn-success btn-login" tag="button"/>
                </div>
                <!--登陆后-->
                <div class="login" style="display: none;">
                    <i class="icon-person">
                        <img width="100%" height="100%" src="${resRoot}/images/avatar.png" id="avatarImg">
                    </i>
                    <p></p>
                    <soul:button target="goTab" skip="4" dataHref="/mine/index.html?channel=mine&skip=4" isLeft="true" text="${views.themes_auto['个人中心']}" opType="function" cssClass="mui-btn mui-btn-success btn-person" tag="button"/>
                </div>
            </div>
            <!--side-nav-->
            <div class="side-nav">
                <ul class="mui-list-unstyled">
                    <li class="home ${empty skip && empty path?'active':''}"><soul:button target="goTab" isLeft="true" skip="2" text="${views.themes_auto['首页']}" dataHref="/mainIndex.html" opType="function"/></li>
                    <li class="pro ${skip == 1?'active':''}"><soul:button target="goTab" text="${views.themes_auto['优惠活动']}" isLeft="true" skip="1" dataHref="/discounts/index.html?skip=1" opType="function" cssClass=""/></li>
                    <li class="download"><soul:button target="goUrl" dataHref="/downLoad/downLoad.html" isLeft="true" text="${views.themes_auto['下载客户端']}" opType="function"/></li>
                    <li class="pc"><soul:button target="goPC" isLeft="true" opType="function" text="${views.themes_auto['电脑版']}"/> </li>
                    <li class="trans"> <soul:button target="goUrl" dataHref="/transfer/index.html" isLeft="true" text="${views.themes_auto['转账']}" opType="function"/></li>
                    <li class="deposit"><soul:button target="goTab" skip="0" isLeft="true" dataHref="/wallet/deposit/index.html" text="${views.themes_auto['账户存款']}" opType="function"/></li>
                    <li class="about ${path == 'about'?'active':''}"><soul:button target="goUrl" dataHref="/mainIndex.html?path=about" isLeft="true" text="${views.themes_auto['关于我们']}" opType="function"/></li>
                    <li class="question"><soul:button target="goUrl" dataHref="/help/firstType.html" isLeft="true" text="${views.themes_auto['常见问题']}" opType="function"/></li>
                    <li class="service"><soul:button target="goTab" isLeft="true" text="${views.themes_auto['在线客服']}" skip="3" dataHref="" opType="function"/></li>
                    <li class="reg_rules ${path == 'terms'?'active':''}"><soul:button target="goUrl" dataHref="/mainIndex.html?path=terms" isLeft="true" text="${views.themes_auto['注册条款']}" opType="function"/></li>
                    <li class="lang ${fn:replace(language, '_', '-')}">
                        <%--<soul:button target="lang" text="${views.themes_auto['语言']}" opType="function"/>--%>
                        <a>${views.themes_auto['语言']}</a>
                    </li>
                </ul>
                <div class="login">
                    <soul:button target="logout" text="${views.themes_auto['退出登录']}" opType="function" cssClass="mui-btn mui-btn-success btn-logout"></soul:button>
                </div>
            </div>
        </div>
    </div>
</aside>
<%@include file="LangMenu.jsp"%>