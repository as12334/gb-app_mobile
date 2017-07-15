<%--@elvariable id="command" type="so.wwb.gamebox.model.company.site.vo.VSiteApiListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<!-- 真人视讯 -->
<input type="hidden" id="api-type" value="live" />
<div class="mui-row">
    <div class="live-list">
        <div class="mui-row">
            <c:forEach var="a" items="${command}">
            <div class="mui-col-xs-6 mui-col-sm-3">
                <a class="item _api" data-api-type-id="1" data-api-id="${a.apiId}" data-status="${api.get(a.apiId.toString()).systemStatus eq 'maintain' ? 'maintain' : siteApi.get(a.apiId.toString()).systemStatus}"
                    data-id="${a.apiId == 3 ? '30455' : ''}" data-game-code="${a.apiId == 3 ? '1183' : ''}" data-game-id="0">
                    <img src="${resRoot}/images/api/api-live-${a.apiId}.png"/>
                    <div class="ct">
                        <p>
                            <span>
                              <%--  <c:choose>
                                    <c:when test="${a.apiId == 10}">
                                        BB大厅
                                    </c:when>
                                    <c:otherwise>
                                        ${gbFn:getApiName(a.apiId.toString())}
                                    </c:otherwise>
                                </c:choose>--%>
                                 ${gbFn:getApiName(a.apiId.toString())}
                            </span>
                        </p>
                    </div>
                </a>
            </div>
            </c:forEach>
        </div>
    </div>
</div>