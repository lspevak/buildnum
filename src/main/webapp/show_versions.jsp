<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="com.github.buildnum.VersionManager"%>
<%@page import="com.github.buildnum.VersionResponse"%>
<%@page import="java.util.List"%>
<%@page import="com.github.buildnum.Utils"%>
<%
    String action = request.getParameter("action");

    List<VersionResponse> versionResponseList = VersionManager.getInstance().getVersionList();
%>
<jsp:include page="header.jsp" flush="true"/>
<jsp:include page="postheader_refresh.jsp" flush="true"/>

    <H3>Versions</H3>

    <table border="1" class="mytable" cellpadding="1">
         <thead>
             <tr>
                 <th>#</th>
                 <th>Group ID</th>
                 <th>Artifact ID</th>
                 <th>Classifier</th>
                 <th>Version</th>
                 <th>Build</th>
                 <th>Created</th>
                 <th>Updated</th>
             </tr>
         </thead>
         <tbody>
<%
    int i=1;
    for(VersionResponse vr : versionResponseList) {
%>
             <tr>
                 <td><%=i%></td>
                 <td><%= vr.getGroupId()%></td>
                 <td><%= vr.getArtifactId()%></td>
                 <td><%= vr.getArtifactClassifier() == null ? "-" : vr.getArtifactClassifier() %></td>
                 <td><%= vr.getArtifactVersion()%></td>
                 <td><%= vr.getBuildVersion()%></td>
                 <td><%= Utils.formatDate(vr.getCreatedAt())%></td>
                 <td><%= Utils.formatDate(vr.getUpdatedAt())%></td>
             </tr>
<%
        i++;
    }
%>
       </tbody>
   </table>
<jsp:include page="footer.jsp" flush="true"/>