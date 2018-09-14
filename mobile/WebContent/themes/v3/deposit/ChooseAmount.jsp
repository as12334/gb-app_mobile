<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="tit">选择金额</div>
<div class="conv_mone">
    <input type="hidden" name="minMoney" value="${minMoney}">
    <input type="hidden" name="maxMoney" value="${maxMoney}">
    <div class="conv_mone_i">
        <c:choose>
            <c:when test="${minMoney > 100 || maxMoney < 100}">
                <i class="icon_mone mone_100" data-rel='{"mone":100,"opType":"function","target":"baseDeposit.invalidMoney"}'></i>
            </c:when>
            <c:otherwise>
                <i class="icon_mone mone_100" data-rel='{"mone":100,"opType":"function","target":"baseDeposit.quickCheckMoney"}'></i>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="conv_mone_i">
        <c:choose>
            <c:when test="${minMoney > 200 || maxMoney < 200}">
                <i class="icon_mone mone_200" data-rel='{"mone":200,"opType":"function","target":"baseDeposit.invalidMoney"}'></i>
            </c:when>
            <c:otherwise>
                <i class="icon_mone mone_200" data-rel='{"mone":200,"opType":"function","target":"baseDeposit.quickCheckMoney"}'></i>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="conv_mone_i">
        <c:choose>
            <c:when test="${minMoney > 500 || maxMoney < 500}">
                <i class="icon_mone mone_500" data-rel='{"mone":500,"opType":"function","target":"baseDeposit.invalidMoney"}'></i>
            </c:when>
            <c:otherwise>
                <i class="icon_mone mone_500" data-rel='{"mone":500,"opType":"function","target":"baseDeposit.quickCheckMoney"}'></i>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="conv_mone_i">
        <c:choose>
            <c:when test="${minMoney > 1000 || maxMoney < 1000}">
                <i class="icon_mone mone_1000" data-rel='{"mone":1000,"opType":"function","target":"baseDeposit.invalidMoney"}'></i>
            </c:when>
            <c:otherwise>
                <i class="icon_mone mone_1000" data-rel='{"mone":1000,"opType":"function","target":"baseDeposit.quickCheckMoney"}'></i>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="conv_mone_i">
        <c:choose>
            <c:when test="${minMoney > 5000 || maxMoney < 5000}">
                <i class="icon_mone mone_5000" data-rel='{"mone":5000,"opType":"function","target":"baseDeposit.invalidMoney"}'></i>
            </c:when>
            <c:otherwise>
                <i class="icon_mone mone_5000" data-rel='{"mone":5000,"opType":"function","target":"baseDeposit.quickCheckMoney"}'></i>
            </c:otherwise>
        </c:choose>
    </div>
</div>
