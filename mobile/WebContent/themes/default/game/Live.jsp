<%--@elvariable id="command" type="java.util.List<so.wwb.gamebox.model.company.site.po.SiteApiRelation>"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<!-- 真人视讯 -->
<input type="hidden" id="api-type" value="live" />
<div class="mui-row">
    <div class="live-list">
        <div class="mui-row">
            <c:forEach var="a" items="${command}">
            <div class="mui-col-xs-6 mui-col-sm-3">
                <a class="item _api" data-api-type-id="1" data-api-id="${a.apiId}" data-status="${a.apiStatus}"
                    data-id="${a.apiId == 3 ? '30455' : ''}" data-game-code="${a.apiId == 3 ? '1183' : ''}" data-game-id="0">
                    <c:set var="path" value="${resRoot}/images/api/api-live-${a.apiId}.png"></c:set>
                    <img src="${soulFn:getImagePath(domain, path)}"/>
                    <div class="ct">
                        <p>
                            <span>
                                 ${a.name}
                            </span>
                        </p>
                    </div>
                </a>
            </div>
            </c:forEach>
        </div>
    </div>
</div>
