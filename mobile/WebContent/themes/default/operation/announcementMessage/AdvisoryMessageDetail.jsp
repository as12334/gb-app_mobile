<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>

<!--我的消息详情页-->
<div class="wrap">
    <div class="notice">
        <div class="notice-left"><em class="path"></em></div>
        <div class="path-right"></span><a>${views.operation_auto['消息公告']}</a><span class="arrow">></span>${views.operation_auto['站点信息']}
        </div>
    </div>

    <div class="return">
        <a data-toggle="button" class="btn btn-gray btn-big fontsmall" name="returnMain" nav-target="mainFrame"
                   type="button" href="/operation/pAnnouncementMessage/advisoryMessage.html">${views.operation_auto['返回列表']}</a>
    </div>

    <!--选项卡-->
    <div class="tabmenu">
        <ul class="tab">
            <li><a href="/operation/pAnnouncementMessage/gameNotice.html" class="gameAnn" nav-target="mainFrame">${views.operation_auto['游戏公告']}</a></li>
            <li><a href="/operation/pAnnouncementMessage/systemNoticeHistory.html" class="systemAnn" nav-target="mainFrame">${views.operation_auto['系统公告']}</a></li>
            <li><a href="javascript:void(0)" class="current siteMessage">${views.operation_auto['站点消息']}</a></li>
        </ul>
    </div>
    <div class="tabmenu tabmenu-2">
        <ul class="tab-2">
            <a class="returnMain" style="display: none" href="/operation/pAnnouncementMessage/advisoryMessage.html" nav-target="mainFrame"></a>
            <li><a href="/operation/pAnnouncementMessage/messageList.html" class="current" nav-target="mainFrame">${views.operation_auto['系统消息']}
                <c:if test="${length gt 0}">
                    <i class="ci-count">${length}</i>
                </c:if>
            </a>
            </li>
            <li><a href="/operation/pAnnouncementMessage/advisoryMessage.html" class="current active" nav-target="mainFrame">${views.operation_auto['我的消息']}
                <c:if test="${advisoryUnReadCount gt 0}">
                    <i class="ci-count menutwo">${advisoryUnReadCount}</i>
                </c:if>
            </a></li>
            <li><a href="/operation/pAnnouncementMessage/beforeSendMessage.html" class="current" nav-target="mainFrame">${views.operation_auto['发送消息']}</a></li>
        </ul>
    </div>

    <!--我的消息详情页-->
    <div class="mymessagemore">

        <c:forEach items="${command}" var="ad">
            <div class="question">
                <h2><i class="problem">${ad.questionType eq '2'?views.operation_auto['追问']:views.operation_auto['提问']}</i>
                        <c:out value="${ad.advisoryTitle}" escapeXml="true" />
                    <span class="date">${dicts.player.advisory_type[ad.advisoryType]}&nbsp;&nbsp;|&nbsp;&nbsp;${soulFn:formatDateTz(ad.advisoryTime, DateFormat.DAY_SECOND,timeZone)}</span>
                </h2>

                <p class="proble-text"><c:out value="${ad.advisoryContent}" escapeXml="true" /></p>
            </div>

            <c:forEach items="${map}" var="s">
                <c:if test="${s.key==ad.id}">
                    <c:forEach items="${s.value.result}" var="ss">
                        <div class="answer">
                            <i class="triangle blue"></i>

                            <h2><i class="reply">${views.operation_auto['回复']}</i><c:out value="${ad.advisoryTitle}" escapeXml="true" /><span class="date">${views.operation_auto['管理员回复']}&nbsp;&nbsp;|&nbsp;&nbsp;${soulFn:formatDateTz(ss.replyTime, DateFormat.DAY_SECOND,timeZone)}</span>
                            </h2>

                            <p class="proble-text"><c:out value="${ad.replyContent}" escapeXml="true" /></p>
                        </div>
                    </c:forEach>
                </c:if>
            </c:forEach>
            <p class="dottline far"></p>

        </c:forEach>

    </div>

    <form method="post">
        <div class="follow-question">
            <div id="validateRule" style="display: none">${validate}</div>
            <div class="follow-question">
                <h2>${views.operation_auto['继续追问']}</h2>
                <input type="hidden" name="result.advisoryType" value="${command.get(0).advisoryType}"/>
                <input type="hidden" name="result.advisoryTitle" value="${command.get(0).advisoryTitle}"/>
                <input type="hidden" name="result.continueQuizId" value="${command.get(0).id}"/>
                <input type="hidden" name="result.continueQuizCount" value="${command.get(0).continueQuizCount}"/>

                <div class="control-group">
                    <label class="control-label">${views.operation_auto['标题']}：</label>
                    <div class="controls-text">${views.operation_auto['【追问】']}<c:out value="${command.get(0).advisoryTitle}" escapeXml="true" /></div>
                </div>

                <div class="control-group">
                    <label class="control-label">${views.operation_auto['内容']}：</label>
                    <div class="controls">
                        <textarea class="textarea-big" maxlength="2000" name="result.advisoryContent"
                                  style="height: 180px;" placeholder="${views.operation_auto['至少输入1个字符']}"></textarea>
                        <p class="character pump">0/2000</p>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="code">${views.common['verificationCode']}</label>
                    <div class="controls-text">
                        <input name="code" id="code" class="input"/>
                        <img class="captcha-code" style="height: 32px;" src="${root}/captcha/continueQuestion.html?t=${random}" reloadable>
                    </div>
                </div>
                <div class="submit">
                    <soul:button style="height:36px" precall="validateForm" cssClass="btn btn-blue middle-big"
                                 text="${views.operation_auto['提交']}" opType="function"
                                 target="subAdvisoryMessage" callback="saveCallbak"/>

                    <soul:button style="height:36px" target="clearPumpText" text="${views.common_report['取消']}"
                                 cssClass="btn btn-blue middle-big" opType="function"/>
                </div>
            </div>
        </div>
    </form>
</div>
<!--继续追问-->

<soul:import res="site/operation/message/sendMessage"/>

