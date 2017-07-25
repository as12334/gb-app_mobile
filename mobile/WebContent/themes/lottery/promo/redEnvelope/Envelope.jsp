<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<div id="containerOut" style="display: none">
    <!--容器1-->
    <input type="hidden" id="lottery_type">
    <input type="hidden" id="win_id">
    <input type="hidden" id="record_id">
    <input type="hidden" id="activity_message_id" value="">
    <input type="hidden" id="applyId" value="">
    <input type="hidden" name="gb.token" value="">
    <div id="container" class="divBg">
        <!--抽奖页面-->
        <div id="lotteryPage" class="divBg divClass" >
            <input class="inputClass btnFont" onclick="closePage('containerOut','')" id="lotteryPageBtn_0" value="" type="button"/>
            <input class="inputClass btnFont" id="lotteryPageBtn_1" value="" onclick="lottery()" type="button"/>
            <div style="position: absolute;top: 360px;width: 100%">
                <div style="text-align: center;font-size: 18px;color: #fff;" id="tip-msg">
                    ${views.promo_auto['剩余抽奖次数']}
                </div>
                <div style="text-align: center;font-size: 18px;color: #fff;" id="lottery_time_tip-msg" class="mui-hide">
                    ${views.promo_auto['下次开始时间']}<br><span id="next_lottery_time"></span>
                </div>
            </div>
        </div>
        <!--中奖页面-->
        <div id="haveAwardPage" class="divBg divClass t-10">
            <input class="inputClass btnFont" onclick="onceAgain()" id="haveAwardPageBtn_0" value="" type="button"/>
            <p class="ab" id="awardname">
                ${views.promo_auto['获得了0元']}
            </p>
            <input class="inputClass btnFont" onclick="applyMoney()" id="haveAwardPageBtn_2" value="" type="button"/>
        </div>
        <!--规则页面-->
        <div class="divBg divClass" id="explainPage">
            <div id="explainContent" class="ab">
            </div>
            <input class="inputClass btnFont" onclick="closePage('explainPage','lotteryPage')" id="explainPageBtn_0" value="" type="button"/>
        </div>x
        <!--未中奖页面-->
        <div class="divBg divClass t-10" id="noAwardPage">
            <input class="inputClass btnFont" onclick="onceAgain()" id="noAwardPageBtn_0" value="" type="button"/>
            <input class="inputClass btnFont" onclick="onceAgain()" id="noAwardPageBtn_1" value="" type="button"/>
        </div>
        <!--领取页面-->
        <div class="divBg divClass" id="rewardPage">
            <input class="inputClass btnFont" onclick="onceAgain()" id="rewardPageBtn_0" value="" type="button"/>
        </div>
        <!--提示-->
        <div class="divBg divClass" id="hitPage" onclick="closePage('hitPage','')">
        </div>
    </div>
</div>
