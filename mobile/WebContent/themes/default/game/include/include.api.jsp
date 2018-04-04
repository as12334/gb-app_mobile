<%--@elvariable id="apiList" type="java.util.List<so.wwb.gamebox.model.company.site.po.SiteApiRelation>"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<div class="mui-row">
    <div class="index-block hb-block">
        <div class="index-block-header mui-hide">
            <div class="mui-pull-left title">${views.game_auto['喜好定制']}</div>
            <div class="mui-pull-right more">
                <a href="#">
                    <span class="mui-icon mui-icon-plus"></span>${views.game_auto['自定义']}
                </a>
            </div>
        </div>
        <div class="index-block-content">
            <c:set var="apiListSize" value="${fn:length(apiList)}"></c:set>
            <c:set var="count" value="0"/>
            <c:set var="hasBBIN" value="false"/><%--BBIN只显示一个--%>
            <c:forEach var="a" items="${apiList}" varStatus="status"><%--BBIN只显示一个--%>
                <c:if test="${a.apiId==10&&hasBBIN eq 'false' || a.apiId!=10}">
                    <c:if test="${count % 3 == 0}">
                        <div class="mui-row">
                    </c:if>
                    <c:set var="apiStatus" value="${a.apiStatus}"></c:set>
                    <div class="mui-col-xs-4">
                        <a href="#" class="_api"
                           data-api-id="${a.apiId}"
                           data-api-type-id="${a.apiTypeId}"
                           data-status="${apiStatus}">
                            <c:choose>
                                <c:when test="${centerId == -3 && a.apiId == 22}">
                                    <c:set var="api_icon" value="4-22-2" />
                                </c:when>
                                <c:otherwise>
                                    <c:set var="api_icon" value="${a.apiTypeId}-${a.apiId}" />
                                </c:otherwise>
                            </c:choose>
                            <span class="api-icon api-icon-${api_icon} site${siteId}" ></span>
                            <span class="icon-text">
                            <c:choose>
                                <c:when test="${a.apiId eq 10}">${a.siteApiName}</c:when>
                                <c:otherwise>${a.name}</c:otherwise>
                            </c:choose>
                        </span>
                        </a>
                    </div>
                    <c:if test="${status.index==apiListSize-1||count%3==2}">
                        </div>
                    </c:if>
                    <c:set var="count" value="${count+1}"/>
                    <c:if test="${a.apiId==10}">
                        <c:set var="hasBBIN" value="true"/>
                    </c:if>
                </c:if>
            </c:forEach>
            <%--<!-- 更多的喜好 -->
            <div class="mui-row">
                <div class="mui-col-xs-4"><a href=""><span class="icon-hobby-nyx"></span><span class="icon-text">NYX</span></a></div>
                <div class="mui-col-xs-4"><a href=""><span class="icon-hobby-hb"></span><span class="icon-text">HB</span></a></div>
                <div class="mui-col-xs-4"><a href=""><span class="icon-hobby-hg"></span><span class="icon-text">HG</span></a></div>
                <div class="mui-col-xs-4"><a href=""><span class="icon-hobby-im"></span><span class="icon-text">IM</span></a></div>
                <div class="mui-col-xs-4"><a href=""><span class="icon-hobby-sb"></span><span class="icon-text">SB</span></a></div>
                <div class="mui-col-xs-4"><a href=""><span class="icon-hobby-kg"></span><span class="icon-text">KG</span></a></div>
                <!-- <div class="mui-col-xs-4"><a href=""><span class="icon-hobby-chq"></span><span class="icon-text">CHQ</span></a></div> -->
            </div>--%>
        </div>
    </div>
</div>
