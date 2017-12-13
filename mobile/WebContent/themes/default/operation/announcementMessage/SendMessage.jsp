<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>

        <div class="notice">
            <div class="notice-left"><em class="path"></em></div>
            <div class="path-right"><a class="cursor">${views.sysResource['消息公告']}</a><span class="arrow">></span>${views.operation_auto['站点消息']}</div>
        </div>

        <!--选项卡-->
        <div class="tabmenu">
            <ul class="tab">
                <li><a href="/operation/pAnnouncementMessage/gameNotice.html" class="gameAnn" nav-target="mainFrame">${views.operation_auto['游戏公告']}</a></li>
                <li><a href="/operation/pAnnouncementMessage/systemNoticeHistory.html" class="systemAnn" nav-target="mainFrame">${views.operation_auto['系统公告']}</a></li>
                <li><a href="/operation/pAnnouncementMessage/messageList.html" class="current siteMessage" nav-target="mainFrame">${views.operation_auto['站点消息']}</a></li>
            </ul>
        </div>
        <div class="tabmenu tabmenu-2">
            <ul class="tab-2">
                <a class="returnMain" style="display: none" href="/operation/pAnnouncementMessage/advisoryMessage.html" nav-target="mainFrame"></a>
                <li><a href="/operation/pAnnouncementMessage/messageList.html" class="current" nav-target="mainFrame">${views.operation_auto['系统消息']}
                    <c:if test="${length gt 0}">
                        <i class="ci-count">${length}</i>
                    </c:if>
                </a></li>
                <li><a href="/operation/pAnnouncementMessage/advisoryMessage.html" class="current" nav-target="mainFrame">${views.operation_auto['我的消息']}
                    <c:if test="${advisoryUnReadCount gt 0}">
                        <i class="ci-count menutwo">${advisoryUnReadCount}</i>
                    </c:if>
                </a></li>
                <li><a href="/operation/pAnnouncementMessage/beforeSendMessage.html" class="current active" nav-target="mainFrame">${views.operation_auto['发送消息']}</a></li>
            </ul>
        </div>


        <!--发送消息-->
        <form method="post">
            <div id="validateRule" style="display: none">${validate}</div>
            <div class="follow-question">
                <div class="control-group">
                    <label class="control-label" style="line-height: 12px;">${views.operation_auto['问题类型']}：</label>
                    <gb:selectPure name="result.advisoryType" cssClass="controls pt-0" list="${advisoryType}"
                                   value="${command.search.advisoryType}"/>
                </div>

                <div class="control-group">
                    <label class="control-label">${views.operation_auto['标题']}：</label>
                    <div class="controls">
                        <input type="text" name="result.advisoryTitle" maxlength="100" class="input-big"
                               placeholder="${views.operation_auto['请输入2-100个字符']}">
                        <p class="character msg">0/100</p>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label">${views.operation_auto['内容']}：</label>
                    <div class="controls">
                    <textarea class="textarea-big" maxlength="2000" name="result.advisoryContent" style="height: 180px;"
                              placeholder="${views.operation_auto['至少输入1个字符']}"></textarea>
                        <p class="character textareaMsg">0/2000</p>
                    </div>
                </div>
                <div class="submit">
                    <soul:button precall="validateForm" style="height:36px" cssClass="btn btn-blue middle-big"
                                 text="${views.operation_auto['提交']}" opType="function"
                                 target="sendAnnouncementMessage" callback="saveCallbak"/>

                    <soul:button style="height:36px" target="clearText" text="${views.common_report['取消']}"
                                 cssClass="btn btn-blue middle-big" opType="function"/>
                </div>
            </div>
        </form>
<soul:import res="site/operation/message/sendMessage"/>
