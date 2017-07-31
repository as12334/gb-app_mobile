<%--@elvariable id="payAccount" type="so.wwb.gamebox.model.master.content.po.PayAccount"--%>
<%--@elvariable id="playerRechargeVo" type="so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<html>
<head>
    <title>${views.deposit_auto['存款']}</title>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <%@ include file="/include/include.base.js.common.jsp" %>
    <script type="text/javascript" src="${root}/mobile/message_<%=SessionManagerCommon.getLocale().toString()%>.js?v=${rcVersion}"></script>
    <script>
        var language = '${language.replace('_','-')}';
        var isLogin = '${isLogin}';
    </script>
    <script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
</head>
<body class="gb-theme mine-page no-backdrop">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav">
            <h1 class="mui-title">${views.deposit_auto['存款']}</h1>
        </header>

        <div class="mui-content mui-scroll-wrapper">
            <div class="mui-scroll">
                <c:set value="${playerRechargeVo.result.rechargeStatus}" var="status"/>
                <c:set value="${playerRechargeVo.result.rechargeType}" var="type"/>
                <c:choose>
                    <%--线上支付--%>
                    <c:when test="${type=='online_deposit'}">
                        <c:set var="url" value="${root}/wallet/deposit/online/index.html"/>
                    </c:when>
                    <%--扫码支付--%>
                    <c:otherwise>
                        <c:set var="url" value="${root}/wallet/deposit/index.html"/>
                    </c:otherwise>
                </c:choose>
                <%--显示文案--%>
                <div class="gb-fund-step">

                </div>


                <div class="submitSuccess">
                    <div class="cont">
                        <div class="hd"></div>
                        <div class="ct">
                            <c:choose>
                                <c:when test="${status=='4'}">
                                    <p>${views.deposit_auto['该笔订单您还未支付']}</p>
                                </c:when>
                                <%--成功--%>
                                <c:when test="${status=='5'}">
                                    <p>${views.deposit_auto['成功存款']}${siteCurrencySign}${soulFn:formatCurrency(playerRechargeVo.result.rechargeAmount)}</p>
                                </c:when>
                                <%--失败--%>
                                <c:when test="${status=='6'}">
                                    <p>${views.deposit_auto['存款失败']}</p>
                                </c:when>
                                <%--超时--%>
                                <c:otherwise>
                                    <p>${views.deposit_auto['订单超时']}</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="ft">
                            <p>
                                <a data-href="${url}" class="mui-btn mui-btn-primary">${views.deposit_auto['再存一次']}</a>
                            </p>
                        </div>
                    </div>
                </div>


            </div>
        </div>
    </div>
</div>
<script>
    mui.init({});
    //主体内容滚动条
    mui('.mui-scroll-wrapper').scroll();

    //选项切换事件
    mui("body").on("tap", "[data-href]", function () {
        var _href = $(this).data('href');
        mui.openWindow({
            url: _href,
            id: _href,
            extras: {},
            createNew: true,
            show: {
                autoShow: true
            },
            waiting: {
                autoShow: true,
                title: '正在加载...'
            }
        })
    });
</script>
</body>
</html>
