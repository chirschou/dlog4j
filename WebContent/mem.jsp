Welcome to tomcat <br/>
<%=request.getRequestURL()%>
<br/>
<%
Runtime lRuntime = Runtime.getRuntime();
out.println("*** MEMORY STATISTICS ***<br/>");
out.println("Free  Memory: "+lRuntime.freeMemory()+"<br/>");
out.println("Max   Memory: "+lRuntime.maxMemory()+"<br/>");
out.println("Total Memory: "+lRuntime.totalMemory()+"<br/>");
out.println("Processors : "+lRuntime.availableProcessors()+"<br/>");
%>
