<%@page contentType="text/html; charset=UTF-8"%>

<script language="JavaScript1.2">
    <!--
        function refresh() {
            window.location.reload(true);
        }
     //-->
</script>

<center>
        <a href="index.jsp">Home</a><br/><br/>

<img src="<%=request.getContextPath()%>/refresh.png" border="0" onclick="javascript: refresh();" title="Refresh page"/><br/><br/>