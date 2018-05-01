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
        <header class="mui-bar mui-bar-nav mui-bar-blue">
            <a class="mui-action-back mui-icon mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">活动申请</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper promo-apply-content content-without-notice  content-without-footer ">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <div class="top_title">
                    <div class="sub_tit">活动初审</div>
                    <div class="tit">${activityName}</div>
                    <input type="hidden" id="resultId" value="${resultId}"/>
                    <input type="hidden" id="code" value="${code}"/>
                    <input type="hidden" id="type" value="${type}"/>
                </div>
                <div class="promo_con_list">
                    <ul class="mui-table-view">
                        <%--<li class="mui-table-view-cell">注册资料完善
                            <span class="icon-pass"></span>
                        </li>
                        <li class="mui-table-view-cell">IP地址唯一性
                            <span class="icon-pass"></span>
                        </li>
                        <li class="mui-table-view-cell">成功绑定银行卡
                            <span class="icon-fail"></span>
                        </li>--%>
                    </ul>
                </div>
                <div class="promo_status">
                    <div class="status_success mui-hidden"> <!--申请成功状态-->
                        <i class="icon-success"></i>
                        <div class="suc_tip">恭喜您！申请成功！</div>
                        <p>您所提交的申请已经进入审批阶段，请及时跟进申请 <br>
                            情况，如有问题请与活动客服联系</p>
                    </div>
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
<script src="${resRoot}/js/discounts/ApplyPromoDetail.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/common/Menu.js?v=${rcVersion}"></script>
</html>
