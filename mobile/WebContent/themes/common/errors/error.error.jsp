<%@ page import="org.soul.commons.init.context.ContextParam" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="org.soul.commons.lang.DateTool" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.soul.commons.lang.string.RandomStringTool" %>
<%@ page import="org.soul.commons.log.Log" %>
<%@ page import="org.soul.commons.exception.ExceptionTool" %>
<%@ page import="org.soul.commons.log.LogFactory" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.base.inc.jsp" %>
<%@ include file="/include/include.base.inc.i18n.jsp" %>
<body class="gb-theme">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav gb-header ${os eq 'android'?'mui-hide':''}">
        <h1 class="mui-title" style="color: #ffffff;">${views.errors_auto['页面不存在']}</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper gb-navbar">
            <div class="mui-scroll">
                <div class="mui-content-padded gb-error">
                    <div class="mui-row m-y-lg">
                        <div class="mui-col-xs-3"></div>
                        <div class="mui-col-xs-6 mui-text-center gb-error-img">
                            <img src="${resRoot}/images/error/ico-404.png">
                        </div>
                        <div class="mui-col-xs-3"></div>
                    </div>
                    <p class="mui-text-center">${views.errors_auto['你访问的页面不存在']}</p>
                    <p class="mui-text-center">Sorry! the page you are looking for doesn't exist.</p>
                    <div class="mui-text-center"><a data-href="${root}/index.html" class="mui-action-back mui-btn mui-btn-primary p-x-lg">${views.errors_auto['返回首页']}</a></div>

                    <%
                        String s="";
                        ContextParam contextParam = CommonContext.get();
                        TimeZone timeZone = contextParam == null ? TimeZone.getTimeZone("GMT+8") : CommonContext.get().getTimeZone();
                        TimeZone timeZone1 = timeZone == null ? TimeZone.getTimeZone("GMT+8") : timeZone;
                        String date = DateTool.formatDate(new Date(),
                                new Locale("zh","CN"),
                                timeZone == null ? TimeZone.getTimeZone("GMT+8") : timeZone,
                                DateTool.yyyy_MM_dd_HH_mm_ss);
                        Exception exception= ((Exception)request.getAttribute("javax.servlet.error.exception"));
                        if(exception!=null) {
                            s = RandomStringTool.randomNumeric(5);
                            Log LOG = LogFactory.getLog(exception.getClass());
                            String stackTrace = ExceptionTool.getStackTrace(exception);
                            if (exception != null && exception.getCause() != null && exception.getCause().getCause() != null) {
                                stackTrace += "\r\nRoot Cause:" + ExceptionTool.getStackTrace(exception.getCause().getCause());
                            }
                            LOG.error("error-" + s + "-" + stackTrace);
                        }
                    %>
                    ${views.errors_common['错误代码']}：<%=s%><br>
                    ${views.errors_common['错误代码提示']}<br>(<%=date%>(<%=timeZone1.getID()%>))
                </div>
            </div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>

    <header>
        <div class="nav-box public-nav _topOri" style="background:#06815d">
            <div class="container">

            </div>
        </div>
    </header>
    <footer class="footer">
        <div class="container _footerOri">

        </div>
    </footer>

</div>

</body>

<script>
    mui('.mui-scroll-wrapper').scroll();
    mui.init({});
    mui("body").on("tap", "[data-href]", function() {
        var _href = $(this).data('href');
        window.location.replace(_href);
    })
</script>

