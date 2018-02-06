<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap index-canvas-wrap">
    <div class="mui-icon mui-icon-closeempty"></div>
    <!-- 菜单容器 -->
    <aside class="mui-off-canvas-left">
        <div class="mui-scroll-wrapper side-menu-scroll-wrapper">
            <div class="mui-scroll">
                <!-- 菜单具体展示内容 -->
                <!--个人信息部分-->
                <div class="person-info">
                    <!--登录前-->
                    <div class="un-login">
                        <p>${views.themes_auto['欢迎观临']}</p>
                        <button data-rel='{"target":"${root}/login/commonLogin.html","opType":"href"}' class="mui-btn mui-btn-success btn-login">${views.themes_auto['用户登录']}</button>
                    </div>
                    <!--登陆后-->
                    <div class="login" style="display: none;">
                        <i class="icon-person"></i>
                        <p></p>
                        <a data-rel='{"target":"${root}/mine/index.html?channel=mine&skip=4","opType":"href"}' type="button" class="mui-btn mui-btn-success  btn-person">${views.themes_auto['个人中心']}</a>
                    </div>
                </div>
                <!--side-nav-->
                <div class="side-nav">
                    <ul class="mui-list-unstyled">
                        <li class="home ${empty skip && empty path?'active':''}"><a data-rel='{"target":"${root}/mainIndex.html","opType":"href"}'>首页</a></li>
                        <li class="question"><a data-rel='{"target":"${root}/help/firstType.html","opType":"href"}'>常见问题</a></li>
                        <li class="reg_rules ${path == 'terms'?'active':''}"><a data-rel='{"target":"${root}/getRegisterRules.html?path=terms","opType":"href"}'>注册条款</a></li>
                        <li class="about ${path == 'about'?'active':''}"><a data-rel='{"target":"${root}/about.html?path=about","opType":"href"}'>关于我们</a></li>
                        <li class="download"><a data-rel='{"target":"downLoadApp","opType":"function"}'>下载客户端</a></li>
                        <li class="pc"><a data-rel='{"target":"goPC","opType":"function"}'>电脑版</a></li>
                        <li class=" lang zh-CN">
                            <a href="">${views.themes_auto['语言']}</a>
                        </li>
                    </ul>
                    <a data-rel='{"target":"logout","opType":"function"}' type="button" class="mui-btn mui-btn-success btn-logout">${views.themes_auto['退出登录']}</a>
                </div>
            </div>
        </div>
        <!--语言弹窗-->
      <%--  <ul class="lang-menu">
            <c:set var="siteLang" value="<%=Cache.getAvailableSiteLanguage()%>"/>
            <c:forEach var="i" items="${siteLang}">
                <li class="${fn:replace(i.value.language, '_', '-')} ${language eq i.value.language?' current':''}">
                    <soul:button text="" lang="${fn:replace(i.value.language, '_', '-')}" target="changeLanguage" opType="function">${dicts.common.language[i.value.language]}</soul:button>
                </li>
            </c:forEach>
        </ul>--%>
    </aside>
</div>