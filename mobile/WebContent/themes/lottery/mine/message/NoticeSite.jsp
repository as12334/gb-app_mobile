<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<div class="mui-row" style="z-index: 123;margin-top: 52px;position: relative;background: white">
    <div class="gb-noticetabs">
        <div id="segmentedControl2" class="mui-segmented-control" style="overflow: auto">
            <a data-href="noticeSite1"
               class="mui-control-item <c:if test="${empty unReadType || unReadType eq 'sysMessage'}">mui-active</c:if>">${views.mine_auto['系统消息']}
                ${not empty sysMessageUnReadCount&&sysMessageUnReadCount!=0?'<span class="unread2-count-icon"></span>':''}</a>
            <a data-href="noticeSite2" class="mui-control-item ${unReadType eq 'advisoryMessage'?'mui-active':''}">${views.mine_auto['我的消息']}
                ${not empty advisoryUnReadCount&&advisoryUnReadCount!=0?'<span class="unread2-count-icon"></span>':''}</a>
            <a data-href="noticeSite3" class="mui-control-item ${unReadType eq 'sendMessage'?'mui-active':''}">${views.mine_auto['发送消息']}</a>
        </div>
    </div>
</div>

<div id="noticeSite1"
     class="mui-control-content <c:if test="${empty unReadType || unReadType eq 'sysMessage'}">mui-active</c:if>"
     name="noticeSite">
    <form name="site1">
        <div class="operate-message">
            <p>
                <a href="" style="color: #333" name="site1_allCheck"><span class="gb-checkbox2"></span>${views.common_report['全选']}</a>
                <button type="button" class="btn mui-btn mui-btn-outlined" style="margin-left: 25px;"
                        id="deleteMessage" name="delete">${views.mine_auto['删除']}
                </button>
                <button type="button" class="btn mui-btn mui-btn-outlined" style="margin-left: 10px;"
                        id="getSelectSystemMessageIds" name="editStatus">${views.mine_auto['标记已读']}
                </button>
            </p>
        </div>
        <div class="mui-scroll-wrapper" id="noticeSite1Scroll" style="position: static;height:84%;">
            <div class="mui-scroll">
                <div class="mui-row">
                    <div class="notice-list">
                        <ul id="noticeSite1Partial">
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<div id="noticeSite2" class="mui-control-content ${unReadType eq 'advisoryMessage'?'mui-active':''}" name="noticeSite">
    <form name="site2">
        <div class="operate-message">
            <p>
                <a href="" style="color: #333" name="site2_allCheck"><span class="gb-checkbox2"></span>${views.common_report['全选']}</a>
                <button type="button" class="btn mui-btn mui-btn-outlined" style="margin-left: 25px;"
                        name="delete">${views.mine_auto['删除']}
                </button>
                <button type="button" class="btn mui-btn mui-btn-outlined" style="margin-left: 10px;"
                        name="editStatus">${views.mine_auto['标记已读']}
                </button>
            </p>
        </div>
        <div class="mui-scroll-wrapper" id="noticeSite2Scroll" style="position: static;height:84%;">
            <div class="mui-scroll">
                <div class="mui-row">
                    <div class="notice-list">
                        <ul id="noticeSite2Partial">

                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>
<div id="noticeSite3" class="mui-control-content ${unReadType eq 'sendMessage'?'mui-active':''}" name="noticeSite">
    <div class="mui-scroll-wrapper" style="position: static;height:70%" id="sendMessageScroll">
        <div class="mui-scroll" id="sendMessage">

        </div>
    </div>
</div>
