<%--@elvariable id="messageVo" type="so.wwb.gamebox.model.master.operation.vo.MobileActivityMessageVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.poppicker.css"/>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css"/>
</head>

<body class="exchange">
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <header class="mui-bar mui-bar-nav">
        <a data-rel='{"target":"goToLastPage","opType":"function"}' class="mui-icon mui-icon-left-nav mui-pull-left"></a>
        <h1 class="mui-title">${views.mine_auto['额度转换']}</h1>
    </header>

    <div class="mui-content mui-scroll-wrapper">
        <div class="mui-scroll">
            <form id="transferForm" class="m-b-0" onsubmit="return false">
                <div id="validateRule" style="display: none">${validateRule}</div>
                <gb:token/>
            <div class="mui-row">

              <div class="mui-input-group mine-form">
                  <div class="bankitem">
                      <p class="mui-text-left" style="padding: 12px;">
                              ${views.transfer_auto['转账处理中']}：<span class="text-green">${player.currencySign}${soulFn:formatCurrency(transferPendingAmount)}</span>
                      </p>
                  </div>
              </div>

                <div class="mui-input-group mine-form m-t-sm">
                    <div class="mui-input-row"><label>${views.transfer_auto['转出账户']}</label>
                        <div class="ct">
                            <div class="gb-select width-all">
                                <a data-href="#" class="mui-btn mui-btn-link gb-input-link turn" default="${views.transfer_auto['我的钱包']}" defaultValue="wallet" data-value="transferInto" id="transferOut">
                                    <span>${views.transfer_auto['我的钱包']}</span>
                                    <input name="transferOut" value="wallet" type="hidden"/>
                                </a>
                            </div>
                            <i class="arrow"></i>
                        </div>
                    </div>
                    <div class="mui-input-row"><label>${views.transfer_auto['转入账户']}</label>
                        <div class="ct">
                            <div class="gb-select width-all">
                                <a data-href="#" class="mui-btn mui-btn-link gb-input-link turn" default="${views.transfer_auto['请选择']}" data-value="transferOut" id="transferInto">
                                    <span>${views.transfer_auto['请选择']}</span>
                                    <input name="transferInto" value="" type="hidden"/>
                                </a>
                            </div>
                            <i class="arrow"></i>
                        </div>
                    </div>
                    <div class="mui-input-row"><label>${views.transfer_auto['金额']}</label>
                        <div class="ct">
                            <input type="number" onkeypress="return (/[\d]/.test(String.fromCharCode(event.keyCode)))" class="gb-money" placeholder="${views.transfer_auto['请输入']}" name="result.transferAmount" autocomplete="off">
                        </div>
                    </div>
                </div>
            </div>

            <div class="mui-row">
                <div class="gb-form-foot"  class="_api" id="mui-content-padded">
                    <a data-rel='{"target":"submitTransactionMoney","opType":"function"}' class="mui-btn mui-btn-primary submit">${views.transfer_auto['确认提交']}</a>
                </div>
            </div>
            </form>


            <div class="mui-row" style="height: 30px;">
                <button id="refreshAllApiBalance" data-rel='{"target":"refreshAllApiBalance", "opType":"function"}' class="mui-btn mui-pull-right"
                        style="margin-right: 10px;font-size: 12px; padding: 3px 10px;line-height: 1.4;">${views.transfer_auto['刷新余额']}
                </button>
            </div>


            <div class="mui-row">
                <div class="mui-input-group mine-form mine-form-nobg">
                    <c:forEach var="api" items="${apis}" varStatus="vs">
                        <c:if test="${api.status!='disable'}">
                            <div class="mui-input-row ${vs.last ? 'final' : ''} _api_${api.id}">
                                <label><span class="_apiName">${api.apiName}</span>:</label>
                                <div class="ct">
                                    <p class="mui-text-right text-gray">
                                        <a class="mui-btn mui-btn-link gb-input-money text-gray">
                                            <c:set var="status" value=""/>
                                            <c:if test="${api.status=='disable'}">
                                                <c:set var="status" value="${views.transfer_auto['暂停转账']}"/>
                                            </c:if>
                                            <c:if test="${api.status=='maintain'}">
                                                <c:set var="status" value="${views.transfer_auto['维护中']}"/>
                                            </c:if>
                                            <span class="_apiMoney_${api.id}">${api.balance}${status}</span>
                                            <span class="mui-icon mui-icon-reload _refresh_api" data-value="${api.id}" data-rel='{"target":"freshApi","dataApiId":"${api.id}" ,"opType":"function"}'></span>
                                        </a>
                                    </p>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <!-- off-canvas backdrop -->
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>
<%@ include file="../include/include.js.jsp" %>
<script src="${resRoot}/js/transfer/Index.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.poppicker.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>
