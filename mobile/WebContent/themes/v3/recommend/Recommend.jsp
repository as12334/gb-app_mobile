<%@ page import="org.soul.commons.currency.CurrencyTool" %>
<%@ page import="org.springframework.ui.Model" %><%--@elvariable id="messageVo" type="so.wwb.gamebox.model.master.operation.vo.MobileActivityMessageVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
</head>

<body class="invite">
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav">
            <a class="mui-action-back mui-icon mui-icon-left-nav"></a>
            <h1 class="mui-title">推荐好友</h1>
            <a href="mine-invite-records.html" class="button mui-btn mui-btn-outlined mui-pull-right" data-href="mine-invite-records.html">推荐记录</a>
        </header>

        <div class="mui-content mui-scroll-wrapper invite-content">
            <div class="mui-scroll">
                <div class="mui-row">
                    <div class="gb-panel">
                        <div class="invite-code">
                            <p class="p1">您的专属推荐码：<span class="text-green">${recommendCode}</span></p>
                            <p class="p2">
                                    <span class="mui-pull-right">
                                        获得红利：<span class="text-green">${sign}${soulFn:formatCurrency(recommend.count)}</span>
                                    </span>
                                <span class="span">
                                        您已推荐好友：<span class="text-green">${recommend.user}名</span>
                                    </span>
                            </p>
                        </div>
                    </div>
                </div>


                <div class="mui-row">
                    <div class="gb-panel m-t-sm">
                        <div class="invite-cont">
                            <div class="cont">
                                <p>这是您的专属邀请码<br>复制以下文字通过QQ等方式发送给好友</p>
                                <div class="link">
                                    <p>${resRoot}${code}</p>
                                </div>
                                <p class="mui-text-center">
                                    <a href="#" id="copyCode" class="btn mui-btn mui-btn-primary" data-clipboard-text="${code}">${views.themes_auto['复制']}</a>

                                </p>
                            </div>

                            <div class="cont">
                                <p><span class="text-blue">推荐奖励</span></p>
                                <c:if test="${reward eq 1}">
                                    <p>推荐好友成功注册并存款满￥30， 双方各获<span class="text-green">${sign}${money}</span>奖励。</p>
                                </c:if>
                                <c:if test="${reward eq 2}">
                                    <p>推荐好友成功注册并存款满￥30， 你将会得到<span class="text-green">${sign}${money}</span>奖励。</p>
                                </c:if>
                                <c:if test="${reward ne 1 && reward ne 2}">
                                    <p>推荐好友成功注册并存款满￥30， 你推荐的好友会获取到<span class="text-green">${sign}${money}</span>奖励。</p>
                                </c:if>
                            </div>

                            <div class="cont">
                                <p><span class="text-blue">推荐红利</span></p>
                                <div class="tabl">
                                    <table>
                                        <tbody><tr>
                                            <td>推荐玩家数量</td>
                                            <td>推荐红利比例</td>
                                        </tr>
                                        <tr>
                                            <td>1</td>
                                            <td>5%</td>
                                        </tr>
                                        <tr>
                                            <td>2</td>
                                            <td>10%</td>
                                        </tr>
                                        <tr>
                                            <td>4</td>
                                            <td>15%</td>
                                        </tr>
                                        <tr>
                                            <td>6</td>
                                            <td>20%</td>
                                        </tr>
                                        </tbody></table>
                                </div>
                                <p></p>
                            </div>

                            <div class="cont">
                                <p><span class="text-blue">活动细则</span></p>
                                <p class="al-left">1. 新用户通过您的邀请链接进行注册、绑定手机后，将立即获得5元红包。您将在第二天获得5元红包。
                                </p><p class="al-left">2. 新用户的手机号码必须是在平安游戏使用过的。</p>
                                <p class="al-left">3. 新用户的手机号码所在地须是平安游戏覆盖城市。</p>
                                <p class="al-left">4. 同一手机号码只在第一次被绑定时赠送红包。每部手机仅限第一次注册时有效。</p>
                                <p class="al-left">5. 如对本活动规则有疑问请联系平安游戏客服。</p>
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
<script type="text/javascript" src="${resRoot}/js/recommend/Recommend.js"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>
