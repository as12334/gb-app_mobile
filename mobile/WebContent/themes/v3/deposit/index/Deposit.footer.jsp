<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../common/Footer.jsp" %>
</body>
<%@ include file="../../include/include.js.jsp" %>
<script src="${resRoot}/js/common/Head.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/Menu.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.poppicker.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/swiper.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/dist/clipboard.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositCenter.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/BaseDeposit.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositOnline.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositCompany.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositScancode.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositBitcoin.js?v=${rcVersion}"></script>
<script type="text/javascript">
    $(function () {
        var obj = $("#list_pay").find(".list_pay_item_w:first");
        var options = obj.attr("data-rel");
        toolBarClick(obj, eval('(' + options + ')'));
    });
</script>