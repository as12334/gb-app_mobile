<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<div class="mui-row">
    <div class="gb-noticetabs">
        <div id="" class="mui-segmented-control">
            <a data-href="noticeSite1" data-rel='{"target":"segmentedControl2","opType":"function"}'
               class="mui-control-item">系统消息</a>
            <a data-href="noticeSite2" data-rel='{"target":"segmentedControl2","opType":"function"}'
               class="mui-control-item">我的消息</a>
            <a data-href="noticeSite3" data-rel='{"target":"segmentedControl2","opType":"function"}'
               class="mui-control-item mui-active">发送消息</a>
        </div>
    </div>
</div>
<div id="noticeSite1"
     class="mui-control-content <c:if test="${empty unReadType || unReadType eq 'sysMessage'}">mui-active</c:if>"
     name="noticeSite">
    <form name="site1">
        <div class="mui-row">
            <div class="notice-list">
                <p>
                    <a href="" style="color: #333" data-rel='{"target":"siteCheck","opType":"function"}'
                       name="site1_allCheck"><span class="gb-checkbox2"></span>${views.common_report['全选']}</a>
                    <button type="button" data-rel='{"target":"deleteSysMessage","opType":"function"}'
                            class="btn mui-btn mui-btn-outlined" style="margin-left: 25px;"
                            id="deleteMessage" name="delete">${views.mine_auto['删除']}
                    </button>
                    <button type="button" data-rel='{"target":"deleteSysMessage","opType":"function"}'
                            class="btn mui-btn mui-btn-outlined" style="margin-left: 10px;"
                            id="getSelectSystemMessageIds" name="editStatus">${views.mine_auto['标记已读']}
                    </button>
                </p>
                <div id="noticeSite1Scroll">
                        <ul id="noticeSite1Partial">

                        </ul>
                </div>
            </div>
        </div>
    </form>
</div>

<div id="noticeSite2" class="mui-control-content ${unReadType eq 'advisoryMessage'?'mui-active':''}" name="noticeSite">
    <form name="site2">

        <div class="mui-row">
            <div class="notice-list">
                <p>
                    <a href="" style="color: #333" data-rel='{"target":"siteCheck","opType":"function"}'
                       name="site2_allCheck"><span class="gb-checkbox2"></span>${views.common_report['全选']}</a>
                    <button type="button" data-rel='{"target":"deleteMyMessage","opType":"function"}'
                            class="btn mui-btn mui-btn-outlined" style="margin-left: 25px;"
                            name="delete">${views.mine_auto['删除']}
                    </button>
                    <button type="button" data-rel='{"target":"deleteMyMessage","opType":"function"}'
                            class="btn mui-btn mui-btn-outlined" style="margin-left: 10px;"
                            name="editStatus">${views.mine_auto['标记已读']}
                    </button>
                </p>
                <div id="noticeSite2Scroll">

                    <ul id="noticeSite2Partial">

                    </ul>
                </div>
            </div>
        </div>

    </form>
</div>

<div id="noticeSite3" class="mui-control-content ${unReadType eq 'sendMessage'?'mui-active':''}" name="noticeSite">
    <div id="sendMessageScroll">
        <div class="mui-scroll" id="sendMessage">

        </div>
    </div>
</div>
