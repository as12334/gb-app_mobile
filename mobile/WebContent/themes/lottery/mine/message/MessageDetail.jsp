<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<html>

<head>
    <title>${messages.common['SystemAnnouncement.'.concat(messageType)]}</title>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>
</head>

<body class="gb-theme mine-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable" style="overflow:visible;">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <%@ include file="/include/include.toolbar.jsp" %>
            <h1 class="mui-title">${messages.common['SystemAnnouncement.'.concat(messageType)]}</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper" style="overflow:visible;height:100%; ${os eq 'android'?'padding-top:0':''}">
                <div class="mui-scroll gb-fund-step notice-detail">
                    <div class="mui-content-padded gb-content">
                        <c:choose>
                            <c:when test="${messageType eq 'sysNotice' || messageType eq 'gameNotice'}">
                                <div style="width:100%; display: block; padding-bottom: 10px">
                                    <b>${vSystemAnnouncementListVo.result.get(0).title}</b>
                                    <i class="mui-pull-right">${soulFn:formatDateTz(vSystemAnnouncementListVo.result.get(0).publishTime, DateFormat.DAY_SECOND,timeZone)}</i>
                                </div>
                                <div class="ct">
                                    <pre style="white-space: pre-wrap;word-wrap: break-word;border: 0px;">${vSystemAnnouncementListVo.result.get(0).content}</pre>
                                </div>
                            </c:when>
                            <c:when test="${messageType eq 'myMessage'}">
                                <c:forEach items="${command}" var="ad">
                                    <div class="question">
                                        <div><i class="problem">${ad.questionType eq '2'?'追问':"提问"}</i>
                                            <span><c:out value="${ad.advisoryTitle}" escapeXml="true"/></span><br>
                                            <span class="date">${dicts.player.advisory_type[ad.advisoryType]}&nbsp;&nbsp;|&nbsp;&nbsp;${soulFn:formatDateTz(ad.advisoryTime, DateFormat.DAY_SECOND,timeZone)}</span>
                                        </div>
                                        <p class="proble-text">${ad.advisoryContent}</p>
                                    </div>
                                    <c:forEach items="${map}" var="s">
                                        <c:if test="${s.key==ad.id}">
                                            <c:forEach items="${s.value.result}" var="ss">
                                                <div class="answer">
                                                    <i class="triangle blue"></i>
                                                    <div><i class="reply">${views.mine_auto['回复']}</i>
                                                        <span><c:out value="${ad.advisoryTitle}" escapeXml="true"/></span><br>
                                                        <span class="date">管理员回复&nbsp;&nbsp;|&nbsp;&nbsp;${soulFn:formatDateTz(ss.replyTime, DateFormat.DAY_SECOND,timeZone)}</span>
                                                    </div>
                                                    <p class="proble-text">${ss.replyContent}</p>
                                                </div>
                                            </c:forEach>
                                        </c:if>
                                    </c:forEach>
                                    <p class="dottline far"></p>
                                </c:forEach>
                            </c:when>
                            <c:when test="${messageType eq 'sysMessage'}">
                                <h4>${command.result.title}</h4><br>
                                <p style="white-space: pre-wrap;word-wrap: break-word;border: 0px;background-color: white">${command.result.content}</p>
                                <div class="al-right co-grayc2 m-b">${soulFn:formatDateTz(command.result.receiveTime, DateFormat.DAY_SECOND,timeZone)}</div>
                            </c:when>
                        </c:choose>
                    </div>
                </div>
        </div>
    </div>
</div>
</body>
<script>
    //主体内容滚动条
    mui('.mui-scroll-wrapper').scroll();
    //data-href
    mui("body").on("tap", "[data-href]", function () {
        var _href = $(this).data('href');
        mui.openWindow({
            url: _href,
            id: _href,
            extras: {},
            createNew: false, //是否重复创建同样id的webview，默认为false:不重复创建，直接显示
            show: {
                autoShow: true, //页面loaded事件发生后自动显示，默认为true
            },
            waiting: {
                autoShow: true, //自动显示等待框，默认为true
                title: '正在加载...', //等待对话框上显示的提示内容

            }
        })
    });
    if(os == 'app_ios'){
        mui("body").on("tap", "[class*='mui-action-back']", function () {
            goBack();
        });
    }
</script>
</html>
