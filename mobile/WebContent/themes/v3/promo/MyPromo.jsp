<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<html>

<head>
    <title>${views.promo_auto['我的优惠记录']}</title>
    <%@ include file="../include/include.head.jsp" %>
</head>

<body>
<div id="offCanvasWrapper" class="mui-off-canvas-wrap">
    <!-- 菜单容器 -->
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <!-- 主页面标题 -->
        <header class="mui-bar mui-bar-nav promo-record">
            <a class="mui-action-back mui-icon mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">${views.promo_auto['我的优惠记录']}</h1>
            <div class="top-info">
                <div>累计获得优惠</div>
                <span class="mone"><c:if test="${empty money}">0.00</c:if>${money}</span>
            </div>
        </header>
        <div class="mui-content mui-scroll-wrapper content-without-notice content-without-footer promo-record-content">
            <!-- 主界面具体展示内容 -->
            <div class="promo-nav">
                <div id="segmentedControl" class="mui-segmented-control">
                    <a data-rel='{"target":"switchOffers","opType":"function"}' class="mui-control-item mui-active"
                       href="#item1">
                        全部优惠
                    </a>
                    <a data-rel='{"target":"switchOffers","opType":"function"}' class="mui-control-item" href="#item2">
                        已派奖
                    </a>
                    <a data-rel='{"target":"switchOffers","opType":"function"}' class="mui-control-item" href="#item3">
                        ${views.promo_auto['未通过']}
                    </a>
                    <a data-rel='{"target":"switchOffers","opType":"function"}' class="mui-control-item" href="#item4">
                        ${views.promo_auto['待审核']}
                    </a>
                </div>
            </div>
            <div class="promo-cont mui-scroll-wrapper" id="refreshContainer">
                <div class="mui-scroll">
                    <%--<%@include file="./MyPromoPartial.jsp"%>--%>
                    <c:set value="${command.result}" var="promo"/>
                    <input type="hidden" value="${command.paging.lastPageNumber}" id="lastPageNumber"/>
                    <div id="item1" class="mui-control-content mui-active">
                        <div class="promo-record-content">
                            <c:choose>
                                <c:when test="${fn:length(promo) > 0}">
                                    <c:forEach items="${promo}" var="s">
                                        <c:choose>
                                            <c:when test="${s.checkState eq '2' || s.checkState eq 'success'}">
                                                <c:set value="green" var="colorAll"/>
                                                <c:set value="${views.promo_auto['已发放']}" var="textAll"/>
                                            </c:when>
                                            <c:when test="${s.checkState eq '3'||s.checkState eq '4'}">
                                                <c:set value="gray" var="colorAll"/>
                                                <c:set value="${views.promo_auto['未通过']}" var="textAll"/>
                                            </c:when>
                                            <c:when test="${s.checkState eq '1'}">
                                                <c:set value="orange" var="colorAll"/>
                                                <c:set value="${views.promo_auto['待审核']}" var="textAll"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set value="" var="textAll"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${fn:length(textAll) > 0}">
                                            <div class="promo-records-warp">
                                                <div class="mui-row">
                                                    <div class="promo-box-left">
                                                        <div>
                                                            <span class="tit">申请时间：</span>${soulFn:formatDateTz(s.applyTime,DateFormat.DAY_SECOND ,timeZone )}
                                                        </div>
                                                        <c:if test="${!empty s.transactionNo}">
                                                            <div>
                                                                <span class="tit">申请单号：</span>${s.transactionNo}
                                                            </div>
                                                        </c:if>
                                                        <div>
                                                            <span class="tit">参与活动：</span>${s.activityName}
                                                        </div>
                                                        <div>
                                                            <span class="tit">优惠稽核：</span><em
                                                                class="org">${soulFn:formatCurrency(s.preferentialAudit)}倍</em>
                                                        </div>
                                                        <i class="pro-bg-left"></i>
                                                    </div>
                                                    <div class="promo-box-right promo-${colorAll}">
                                                        <span class="num">${siteCurrencySign}${soulFn:formatCurrency(s.preferentialValue)}</span>
                                                        <span class="annotation">${textAll}</span>
                                                        <i class="pro-bg-right"></i>
                                                        <div class="round radio-up"></div>
                                                        <div class="round radio-dwon"></div>
                                                    </div>
                                                </div>
                                            </div>
                                            <c:set value="itemAll" var="itemAll"/>
                                        </c:if>
                                    </c:forEach>
                                </c:when>
                            </c:choose>
                            <c:if test="${fn:length(itemAll) <= 0}">
                                <div class="mui-row no-promo">
                                        ${views.promo_auto['暂无优惠信息']}
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <div id="item2" class="mui-control-content">
                        <div class="promo-record-content">
                            <c:choose>
                                <c:when test="${fn:length(promo) > 0}">
                                    <c:forEach items="${promo}" var="s">
                                        <c:choose>
                                            <c:when test="${s.checkState eq '2' || s.checkState eq 'success'}">
                                                <c:set value="green" var="colorSuccess"/>
                                                <c:set value="${views.promo_auto['已发放']}" var="textSuccess"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set value="" var="textSuccess"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${fn:length(textSuccess) > 0}">
                                            <div class="promo-records-warp">
                                                <div class="mui-row">
                                                    <div class="promo-box-left">
                                                        <div>
                                                            <span class="tit">申请时间：</span>${soulFn:formatDateTz(s.applyTime,DateFormat.DAY_SECOND ,timeZone )}
                                                        </div>
                                                        <c:if test="${!empty s.transactionNo}">
                                                            <div>
                                                                <span class="tit">申请单号：</span>${s.transactionNo}
                                                            </div>
                                                        </c:if>
                                                        <div>
                                                            <span class="tit">参与活动：</span>${s.activityName}
                                                        </div>
                                                        <div>
                                                            <span class="tit">优惠稽核：</span><em
                                                                class="org">${soulFn:formatCurrency(s.preferentialAudit)}倍</em>
                                                        </div>
                                                        <i class="pro-bg-left"></i>
                                                    </div>
                                                    <div class="promo-box-right promo-${colorSuccess}">
                                                        <span class="num">${siteCurrencySign}${soulFn:formatCurrency(s.preferentialValue)}</span>
                                                        <span class="annotation">${textSuccess}</span>
                                                        <i class="pro-bg-right"></i>
                                                        <div class="round radio-up"></div>
                                                        <div class="round radio-dwon"></div>
                                                    </div>
                                                </div>
                                            </div>
                                            <c:set value="itemSuccess" var="itemSuccess"/>
                                        </c:if>
                                    </c:forEach>
                                </c:when>
                            </c:choose>
                            <c:if test="${fn:length(itemSuccess) <= 0}">
                                <div class="mui-row no-promo">
                                        ${views.promo_auto['暂无优惠信息']}
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <div id="item3" class="mui-control-content">
                        <div class="promo-record-content">
                            <c:choose>
                                <c:when test="${fn:length(promo) > 0}">
                                    <c:forEach items="${promo}" var="s">
                                        <c:choose>
                                            <c:when test="${s.checkState eq '3'||s.checkState eq '4'}">
                                                <c:set value="gray" var="colorNoPass"/>
                                                <c:set value="${views.promo_auto['未通过']}" var="textNoPass"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set value="" var="textNoPass"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${fn:length(textNoPass) > 0}">
                                            <div class="promo-records-warp">
                                                <div class="mui-row">
                                                    <div class="promo-box-left">
                                                        <div>
                                                            <span class="tit">申请时间：</span>${soulFn:formatDateTz(s.applyTime,DateFormat.DAY_SECOND ,timeZone )}
                                                        </div>
                                                        <c:if test="${!empty s.transactionNo}">
                                                            <div>
                                                                <span class="tit">申请单号：</span>${s.transactionNo}
                                                            </div>
                                                        </c:if>
                                                        <div>
                                                            <span class="tit">参与活动：</span>${s.activityName}
                                                        </div>
                                                        <div>
                                                            <span class="tit">优惠稽核：</span><em
                                                                class="org">${soulFn:formatCurrency(s.preferentialAudit)}倍</em>
                                                        </div>
                                                        <i class="pro-bg-left"></i>
                                                    </div>
                                                    <div class="promo-box-right promo-${colorNoPass}">
                                                        <span class="num">${siteCurrencySign}${soulFn:formatCurrency(s.preferentialValue)}</span>
                                                        <span class="annotation">${textNoPass}</span>
                                                        <i class="pro-bg-right"></i>
                                                        <div class="round radio-up"></div>
                                                        <div class="round radio-dwon"></div>
                                                    </div>
                                                </div>
                                            </div>
                                            <c:set value="itemNoPass" var="itemNoPass"/>
                                        </c:if>
                                    </c:forEach>
                                </c:when>
                            </c:choose>
                            <c:if test="${fn:length(itemNoPass) <= 0}">
                                <div class="mui-row no-promo">
                                        ${views.promo_auto['暂无优惠信息']}
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <div id="item4" class="mui-control-content">
                        <div class="promo-record-content">
                            <c:choose>
                                <c:when test="${fn:length(promo) > 0}">
                                    <c:forEach items="${promo}" var="s">
                                        <c:choose>
                                            <c:when test="${s.checkState eq '1'}">
                                                <c:set value="orange" var="colorUnAudit"/>
                                                <c:set value="${views.promo_auto['待审核']}" var="textUnAudit"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set value="" var="textUnAudit"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${fn:length(textUnAudit) > 0}">
                                            <div class="promo-records-warp">
                                                <div class="mui-row">
                                                    <div class="promo-box-left">
                                                        <div>
                                                            <span class="tit">申请时间：</span>${soulFn:formatDateTz(s.applyTime,DateFormat.DAY_SECOND ,timeZone )}
                                                        </div>
                                                        <c:if test="${!empty s.transactionNo}">
                                                            <div>
                                                                <span class="tit">申请单号：</span>${s.transactionNo}
                                                            </div>
                                                        </c:if>
                                                        <div>
                                                            <span class="tit">参与活动：</span>${s.activityName}
                                                        </div>
                                                        <div>
                                                            <span class="tit">优惠稽核：</span><em
                                                                class="org">${soulFn:formatCurrency(s.preferentialAudit)}倍</em>
                                                        </div>
                                                        <i class="pro-bg-left"></i>
                                                    </div>
                                                    <div class="promo-box-right promo-${colorUnAudit}">
                                                        <span class="num">${siteCurrencySign}${soulFn:formatCurrency(s.preferentialValue)}</span>
                                                        <span class="annotation">${textUnAudit}</span>
                                                        <i class="pro-bg-right"></i>
                                                        <div class="round radio-up"></div>
                                                        <div class="round radio-dwon"></div>
                                                    </div>
                                                </div>
                                            </div>
                                            <c:set value="itemUnAudit" var="itemUnAudit"/>
                                        </c:if>
                                    </c:forEach>
                                </c:when>
                            </c:choose>
                            <c:if test="${fn:length(itemUnAudit) <= 0}">
                                <div class="mui-row no-promo">
                                        ${views.promo_auto['暂无优惠信息']}
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div> <!--mui-content 闭合标签-->
    </div>
</div>
</body>

<%@ include file="../include/include.js.jsp" %>
<script src="${resRoot}/js/discounts/MyPromo.js?v=${rcVersion}"></script>
</html>
