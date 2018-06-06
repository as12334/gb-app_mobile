<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <!-- 主页面标题 -->
        <header class="mui-bar mui-bar-nav">
            <a class="mui-action-back mui-icon mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">活动申请</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper promo-apply-content ${code == 'deposit_send' ? 'promo-apply2-content' : 'promo-apply3-content content-without-footer'} content-without-notice">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <div class="top_title">
                    <div class="sub_tit">活动初审</div>
                    <div class="tit">${activityName}</div>
                    <input type="hidden" id="resultId" value="${resultId}"/>
                    <input type="hidden" id="code" value="${code}"/>
                    <input type="hidden" id="type" value="${type}"/>
                </div>
                <c:choose>
                    <c:when test="${code == 'deposit_send'}">
                        <div class="promo_item_list">
                            <%--<div class="promo_item">
                                <div class="ite">
                                    <span class="ti">存款订单号：</span> CBSISJDHSIDHUIHAD
                                </div>
                                <div class="ite">
                                    <span class="ti">交易时间：</span> 2018-4-20 15:31:37
                                </div>
                                <div class="ite">
                                    <span class="ti">存款金额：</span> ¥ 1000
                                </div>
                                <div class="ite">
                                    <span class="ti">公司入款：</span> 公司入款
                                </div>
                                <div class="promo_item_sta suc">
                                    申请成功
                                </div>
                            </div>
                            <div class="promo_item">
                                <div class="ite">
                                    <span class="ti">存款订单号：</span> CBSISJDHSIDHUIHAD
                                </div>
                                <div class="ite">
                                    <span class="ti">交易时间：</span> 2018-4-20 15:31:37
                                </div>
                                <div class="ite">
                                    <span class="ti">存款金额：</span> ¥ 1000
                                </div>
                                <div class="ite">
                                    <span class="ti">公司入款：</span> 公司入款
                                </div>
                                <div class="promo_item_sta awa">
                                    申请奖励
                                </div>
                            </div>
                            <div class="promo_item">
                                <div class="ite">
                                    <span class="ti">存款订单号：</span> CBSISJDHSIDHUIHAD
                                </div>
                                <div class="ite">
                                    <span class="ti">交易时间：</span> 2018-4-20 15:31:37
                                </div>
                                <div class="ite">
                                    <span class="ti">存款金额：</span> ¥ 1000
                                </div>
                                <div class="ite">
                                    <span class="ti">公司入款：</span> 公司入款
                                </div>
                                <div class="promo_item_sta fai" id="test">
                                    申请失败
                                    <span>（-404）</span>
                                </div>
                            </div>
                            <div class="promo_item">
                                <div class="ite">
                                    <span class="ti">存款订单号：</span> CBSISJDHSIDHUIHAD
                                </div>
                                <div class="ite">
                                    <span class="ti">交易时间：</span> 2018-4-20 15:31:37
                                </div>
                                <div class="ite">
                                    <span class="ti">存款金额：</span> ¥ 1000
                                </div>
                                <div class="ite">
                                    <span class="ti">公司入款：</span> 公司入款
                                </div>
                                <div class="promo_item_sta proc">
                                    申请中...
                                </div>
                            </div>--%>
                        </div>
                    </c:when>
                    <c:when test="${code == 'effective_transaction' || code == 'profit_loss'}">
                        <div class="promo_con_list">
                            <ul class="mui-table-view">
                                <%--<li class="mui-table-view-cell">条件一：盈利1230元
                                    <span class="icon-pass-disa"></span>
                                </li>
                                <li class="mui-table-view-cell">条件一：盈利1230元
                                    <span class="icon-pass"></span>
                                </li>
                                <li class="mui-table-view-cell">条件一：盈利1230元
                                    <span class="icon-fail"></span>
                                </li>--%>
                            </ul>
                        </div>
                        <div class="pro_mone">
                            <div class="mui-pull-left">
                               <%-- 当前盈活亏损：<span class="color-green">¥ -2,300</span>--%>
                            </div>
                            <%--<div class="mui-pull-right">
                                有效投注额：<span class="color-gray">¥ 2,300</span>
                            </div>--%>
                        </div>
                        <div id="unCommit" class="mui-hidden"> <!--未参加状态-->
                            <a class="mui-btn mui-btn-block btn_apply" data-rel='{"target":"applyProfit","opType":"function"}'>参与报名</a>
                            <div class="app_num">
                                <%--已有 <span class="color-blue">3,290</span>人，报名成功--%>
                            </div>
                        </div>
                        <div id="join" class="mui-hidden"> <!--已参加状态-->
                            <a class="mui-btn mui-btn-block btn_apply disabled">已参加</a>
                            <div class="app_num">
                                <%--派奖时间：<span class="color-blue">XXXX年X月X日X时X分</span>--%>
                            </div>
                        </div>
                    </c:when>
                </c:choose>
                <div class="promo_status">
                    <div class="status_failure mui-hidden"> <!--申请失败状态-->
                        <i class="icon-failure"></i>
                        <div class="fai_tip">非常遗憾！尚未满足活动参与条 <br>
                            件申请失败！
                        </div>
                    </div>
                    <a data-rel='{"target":"loadCustomer","opType":"function" }'
                       class="mui-btn mui-btn-block btn_cust_serv mui-hidden">联系客服</a>
                </div>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
    </div>
</div>
</body>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/common/Menu.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/discounts/ApplyPromoDetail.js?v=${rcVersion}"></script>
</html>
