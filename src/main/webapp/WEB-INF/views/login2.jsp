<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>登录</title>
	<link href="${pageContext.request.contextPath}/css/regcss.css" type="text/css" rel="stylesheet"/>
	<script type="text/javascript">
	 function  validate(){   
          if(document.myform.username.value==""){   
                  alert("用户名不能为空");   
                  document.myform.username.focus();   
                  return false;   
          }else 
          if(document.myform.password.value==""){   
                  alert("密码不能为空");   
                  document.myform.password.focus();   
                  return false;   
          }   
         return true;   
     }   
	</script> 
  </head>
<body>
	<div id="content">
		<div id="form">
		  <h1>用户登录</h1><br/>
		  <form action="/user/login" method="post" id="myform"  onsubmit="return validate()">
		      用户名<input type="text"  name="userName" style="width:190px; height:26px; margin-left:39px;"/><br/>
		  	  密码<input type="password" name="password" style="width:190px; height:26px; margin-top:8px;margin-left:54px;"/><br/>
			  <input type="submit" value="登录" style="width:50px; height:30px; margin-top:8px;"/>
			  <a href="/user/register">注册</a>
		  </form>
		  <!-- 显示错误信息 -->
		  <c:if test="${errorMsg!=null}"> 
		      <font color="red">${errorMsg}</font>    
		  </c:if>
		  <!-- 显示提示 -->
		  <c:if test="${noticeMsg!=null}"> 
		      <font color="green">${noticeMsg}</font>    
		  </c:if>
	  </div>
	</div>

	<div>
		<div>
			<h1>获取用户</h1><br/>
			<form action="/user/getAllUsersPage" method="post">
				<input type="hidden" name="startPage" id="startPage" value="${startPage}"/>
				<input type="hidden" name="currentPage" id="currentPage" value="${currentPage}"/>
				<input type="hidden" name="pageSize" id="pageSize" value="${pageSize}"/>
				<input type="hidden" name="totalPage" id="totalPage" value="${totalPage}"/>
				<input type="hidden" name="totalCount" id="totalCount" value="${totalCount}"/>
				<input type="submit" value="获取用户" style="width:50px; height:30px; margin-top:8px;"/>

			</form>

		</div>
	</div>

	<div>
		<div>
			<h1>搜索用户</h1><br/>
			<form action="/user/searchUsersPage" method="post">
				用户名<input type="text"  name="userName" style="width:190px; height:26px; margin-left:39px;"/><br/>
				用户类型<input type="text" name="userStatus" style="width:190px; height:26px; margin-top:8px;margin-left:54px;"/><br/>
				<input type="hidden" name="startPage" value="${startPage}"/>
				<input type="hidden" name="currentPage" value="${currentPage}"/>
				<input type="hidden" name="pageSize" value="${pageSize}"/>
				<input type="hidden" name="totalPage" value="${totalPage}"/>
				<input type="hidden" name="totalCount"  value="${totalCount}"/>
				<input type="submit" value="搜索用户" style="width:50px; height:30px; margin-top:8px;"/>
			</form>
		</div>
	</div>
<%--
	<div>
		<div>
			<h1>搜索书籍</h1><br/>
			<form action="/book/searchBooksPage" method="post">
				书名<input type="text"  name="bookName" style="width:190px; height:26px; margin-left:39px;"/><br/>
				书本类型<input type="text" name="userStatus" style="width:190px; height:26px; margin-top:8px;margin-left:54px;"/><br/>
				价格：<input  name="startPrice" type="number" min="0.0" step="0.1" style="width:60px" value="${startPrice}"/>
				- <input name="endPrice" type="number" min="0.0" step="0.1" style="width:60px" value="${endPrice}"/><br/><br/>
				出版日期：<input type="datetime-local" name="startTime" value="${startTime}"/> - <input type="datetime-local" name="endTime" value="${endTime}"/>
				<input type="hidden" name="startPage" value="${startPage}"/>
				<input type="hidden" name="currentPage" value="${currentPage}"/>
				<input type="hidden" name="pageSize" value="${pageSize}"/>
				<input type="hidden" name="totalPage" value="${totalPage}"/>
				<input type="hidden" name="totalCount"  value="${totalCount}"/>
				<input type="submit" value="搜索书籍" style="width:50px; height:30px; margin-top:8px;"/>
			</form>
		</div>
	</div>--%>

</body>
</html>
