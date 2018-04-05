<%@ page import="org.soul.commons.currency.CurrencyTool" %>
<%@ page import="org.springframework.ui.Model" %><%--@elvariable id="messageVo" type="so.wwb.gamebox.model.master.operation.vo.MobileActivityMessageVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html class="new-invite">
<head>
    <title>${siteName}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no,minimal-ui">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <%@ include file="../include/include.head.jsp" %>
    <link rel="bookmark" href="${resRoot}/favicon.ico">
    <link rel="shortcut icon" href="${resRoot}/favicon.ico">
    <link rel="stylesheet" href="${resRoot}/themes/mui.picker.css" />
    <link rel="stylesheet" href="${resRoot}/themes/mui.dtpicker.css" />
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav">
            <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">推荐好友</h1>
            <a href="javascript:void(0);" class="btn-question" id="question" ></a>
        </header>

        <div class="mui-content mui-scroll-wrapper invite-content">
            <div class="mui-scroll">
                <div class="panel1">
                    <div class="row1">
                        <div>我分享的好友数 <span>${recommend.user}</span>人</div>
                        <div>我的分享奖励 <span>${recommend.single}</span> 元</div>
                    </div>
                    <div class="row2">
                        <div>我的奖励次数 <span>${recommend.count}</span> 次</div>
                        <div>我的分享红利 <span>${recommend.bonus}</span> 元</div>
                    </div>
                </div>
                <div class="panel2">
                    您的专属链接，<br>
                    复制后通过微信、QQ等方式发送给好友
                    <div class="input-wrap">
                        <input type="text" value="${domain}${code}">
                        <a href="#" data-clipboard-text="${domain}${code}" data-rel='{"target":"copyCode","opType":"function"}' class="btn-copy copy">${views.themes_auto['复制']}</a>
                        <input type="hidden" value="${activityRules}" id="activityRules"> <%--接收活动规则，在通过jQuery显示--%>
                    </div>
                </div>

                <div class="panel3">
                    <div class="mui-segmented-control">
                        <a class="mui-control-item mui-active" href="#item1">
                            奖励规则
                        </a>
                        <a class="mui-control-item" href="#item2">
                            分享记录
                        </a>
                    </div>
                    <div id="item1" class="mui-control-content mui-active">
                        <div id="scroll" class="mui-scroll-wrapper">
                            <div class="mui-scroll">
                                <c:if test="${reward eq 1}">
                                    <div class="pan">
                                        <div class="tit">互惠奖励</div>
                                        推荐好友成功注册并存款满${witchWithdraw}元 <br>
                                        双方各获${money}元奖励
                                    </div>
                                </c:if>

                                <c:if test="${bonus eq true}">


                                <div class="pan">
                                    <div class="tit">分享红利</div>
                                    红利=分享好友的有效投注额*分享红利比例
                                    <table class="table1">
                                        <thead>
                                        <tr>
                                            <th>分享好友有效投注人数</th>
                                            <th>分享红利比例</th>
                                        </tr>
                                        </thead>
                                        <tbody>

                                        <c:forEach items="${gradientTempArrayList}" var="p" varStatus="status">
                                            <tr>
                                                <td>${p.playerNum}以上</td><%--分享好友投注人数--%>
                                                <td>${p.proportion} %</td><%--红利比例--%>
                                            </tr>

                                        </c:forEach>

                                        </tbody>
                                    </table>
                                </div>
                                </c:if>

                                
                            </div>
                        </div>
                    </div>
                    <div id="item2" class="mui-control-content share-record-wrap">
                        <div class="filter-p">
                            <div class="gb-datafilter">
                                创建日期:
                                <span class="input-date" ><a href=""></a>
		                <input type="datetime" class="date"
                               value="${soulFn:formatDateTz(defaultMinDate, DateFormat.DAY, timeZone)}"
                               id="beginTime" data-rel='{"target":"clickMinDate","opType":"function"}' minDate="${soulFn:formatDateTz(defaultMinDate, DateFormat.DAY, timeZone)}">
		            </span>
                                ~
                                <span class="input-date"><a href=""></a>
		                <input type="datetime" class="date"
                               value="${soulFn:formatDateTz(defaultMaxDate, DateFormat.DAY, timeZone)}" id="endTime"
                               endTime="${soulFn:formatDateTz(defaultMaxDate, DateFormat.DAY, timeZone)}"
                               minDate="${soulFn:formatDateTz(defaultMinDate, DateFormat.DAY, timeZone)}" data-rel='{"target":"clickMaxDate","opType":"function"}'>
		            </span>
                                <a href="#selectDate" class="btn mui-btn mui-btn-primary btn-kx" style="width: 15%;" data-rel='{"target":"searchBydate","opType":"function"}'>搜索</a>
                                <div class="clearfix"></div>
                            </div>
                        </div>
                        <div  class="mui-scroll-wrapper">
                            <div class="mui-scroll">
                                <div class="sha-wra">
                                    <table class="table2">
                                        <thead>
                                        <tr>
                                            <th>好友账号</th>
                                            <th>注册时间</th>
                                            <th>状态</th>
                                            <th>推荐奖励</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${command}" var="vo">
                                            <tr>
                                                <td>${vo.recommendUserName}</td> <%--被推荐人账号--%>
                                                <td>${soulFn:formatDateTz(vo.createTime, DateFormat.DAY, timeZone)}</td>  <%--注册时间--%>
                                                <td>${vo.status}</td>         <%--状态--%>
                                                <td>${vo.rewardAmount}</td>    <%--推荐奖励--%>
                                            </tr>
                                        </c:forEach>

                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <!-- off-canvas backdrop -->
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>
<%@ include file="../include/include.js.jsp" %>
<script src="${resRoot}/js/mui/mui.picker.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/dist/clipboard.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/recommend/Recommend.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>
