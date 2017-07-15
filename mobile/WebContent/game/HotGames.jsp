<%--@elvariable id="command" type="so.wwb.gamebox.model.company.site.vo.VSiteApiListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<div class="mui-row hot-row">
    <div class="mui-col-xs-6 _game"
         data-api-type-id="${fish.apiTypeId}" data-api-id="${fish.apiId}" data-game-id="${fish.gameId}" data-game-code="${fish.code}" data-status="${fish.status}">
        <div class="game-hot g1">
            <div class="text">
                <div class="title">${views.game_auto['AG捕鱼王']}</div>
                <div class="description">${views.game_auto['百发百中']}</div>
            </div>
            <div class="img">
                <img src="${resRoot}/images/game/game-byw.png"/>
            </div>
        </div>
    </div>
    <div class="mui-col-xs-6" style="padding-left: 0;">
        <div class="mui-row">
            <div class="mui-col-xs-12 _api" data-api-type-id="3" data-api-id="19" data-status="${not empty apiStatus_19?apiStatus_19:'maintain'}">
                <div class="game-hot g2">
                    <div class="text">
                        <div class="title">${views.game_auto['沙巴体育']}</div>
                        <div class="description" style="width: 66%;line-height: 15px;">${views.game_auto['最热门的线上体育']}</div>
                    </div>
                    <div class="img">
                        <img src="${resRoot}/images/game/game-saba.png" style="width: 57px;"/>
                    </div>
                </div>
            </div>
            <div class="mui-col-xs-12 _api" data-api-type-id="1" data-api-id="16" data-status="${not empty apiStatus_19?apiStatus_16:'maintain'}">
                <div class="game-hot g3">
                    <div class="text">
                        <div class="title">${views.game_auto['EBET真人']}</div>
                        <div class="description">${views.game_auto['移动娱乐']}</div>
                    </div>
                    <div class="img">
                        <img src="${resRoot}/images/game/game-zr.png"/>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>
<div class="mui-row normal-row">
    <c:forEach var="hotGame" items="${hotGames}" varStatus="status">
        <c:set value="${gameI18nMap.get(''.concat(hotGame.gameId))}" var="gameI18n"></c:set>
        <div class="mui-col-xs-3 mui-col-sm-8-1 _game"
             data-api-type-id="${hotGame.apiTypeId}" data-api-id="${hotGame.apiId}"
             data-game-id="${hotGame.apiId eq 10?'':hotGame.gameId}" data-game-code="${hotGame.apiId eq 10?'':hotGame.code}" data-status="${hotGame.status}">
            <img src="${soulFn:getImagePath(domain, gameI18n.cover)}"/>
            <span class="name">${gameI18n.name}</span>
        </div>
    </c:forEach>
</div>