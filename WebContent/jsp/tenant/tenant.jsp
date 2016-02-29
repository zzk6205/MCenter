<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/jsp/public/header.jsp"%>
<html>
<head>
<title>List</title>
<script type="text/javascript">
$(function() {
	query();
});
// 查询
function query() {
	$('#tenantList').datagrid({
		title : '企业列表',
		method : 'post',
		singleSelect : false,
		height : 370,
		fit : false,
		fitColumns : true,
		striped : true,
		collapsible : true,
		url : "list",
		sortName : 'TENANT_ID',
		sortOrder : 'asc',
		remoteSort : false,
		idField : 'TENANT_ID',
		pagination : true, // 显示分页
		rownumbers : true, // 显示行号
		queryParams:{tenantNameSearch:$("#tenantNameSearch").val(),tenantSpellSearch:$("#tenantSpellSearch").val()},
		columns : [ [ 
            { field : 'ck', checkbox : true, width : 2 },
			{ field : 'TENANT_ID', title : '企业编码', width : 20, sortable : true, halign : 'center' },
			{ field : 'TENANT_NAME', title : '企业名称', width : 20, sortable : true, halign : 'center' },
			{ field : 'TENANT_SPELL', title : '拼音码', width : 20, sortable : true, halign : 'center' }
		] ],
		toolbar : [ {
			text : '更新',
			iconCls : 'icon-edit',
			handler : function() {
				forUpdate();
			}
		}, '-', {
			text : '删除',
			iconCls : 'icon-remove',
			handler : function() {
				forDelete();
			}
		}, '-', {
			text : '导出',
			iconCls : 'icon-print',
			handler : function() {
				forExport();
			}
		} ],
		onLoadSuccess : function() {
			$('#tenantList').datagrid('clearSelections'); // 一定要加上这一句，要不然datagrid会记住之前的选择状态，删除时会出问题
		}
	});
}
// 更新
function forUpdate() {
	var rows = $('#tenantList').datagrid('getSelections');
	if (rows == null || rows == "") {
		$.messager.alert('信息提示', '请选择企业', 'info');
		return;
	}
	if (rows.length != 1) {
		$.messager.alert('信息提示', '只能选择一条记录', 'info');
		return;
	} else {
		$.ajax({
			url : rows[0].TENANT_ID,
			type : "GET",
			dataType : 'json',
			success : function(data) {
				$("#tenantId").textbox("setValue", data.TENANT_ID);
				$("#tenantName").textbox("setValue", data.TENANT_NAME);
				$("#tenantSpell").textbox("setValue", data.TENANT_SPELL);
				$("#editTenantWin").dialog({
					modal : true,
					collapsible : true,
					resizable : true,
					iconCls : 'icon-edit',
					buttons : [ {
						text : '保存',
						iconCls : 'icon-ok',
						handler : function() {
							var tenantId = $('#tenantId').val();
							var tenantName = $('#tenantName').val();
							var tenantSpell = $('#tenantSpell').val();
							if (tenantName == "" || tenantName == null) {
								$.messager.alert('信息提示', '企业名称不能为空！', 'info');
								return false;
							}
							if (tenantSpell == "" || tenantSpell == null) {
								$.messager.alert('信息提示', '拼音码不能为空！', 'info');
								return false;
							}
							var data = $("#editTenantForm").serializeJson();
							data.tenantId = tenantId;
							var url = $('#tenantId').val();
							$.ajax({
								url : url,
								type : "POST",
								datatype : 'json',
								data : data,
								success : function(data) {
									if (data == 'SUCCESS') {
										$('#editTenantWin').dialog('close');
										$('#tenantList').datagrid('reload');
									}
								}
							});
						}
					}, {
						text : '取消',
						iconCls : 'icon-cancel',
						handler : function() {
							$('#editTenantWin').dialog('close');
						}
					} ]
				});
				$('#editTenantWin').dialog('open');
			}
		});
	}
}
// 删除
function forDelete() {
	var rows = $('#tenantList').datagrid('getSelections');
	if (rows == null || rows == "") {
		$.messager.alert('信息提示', '请选择企业', 'info');
		return;
	}
	if (rows.length != 1) {
		$.messager.alert('信息提示', '只能选择一条记录', 'info');
		return;
	} else {
		var url = rows[0].tenantId;
		var param = { '_method' : 'delete' };
		$.ajax({
			url : url,
			type : "POST",
			datatype : 'json',
			data : param,
			success : function(data) {
				if (data == 'SUCCESS') {
					$('#tenantList').datagrid('reload');
				}
			}
		});
	}
}
//导出
function forExport() {
	this.location.href = 'exportExcel';
}
function dateFormatter(date) {
	var y = date.getFullYear();
	var m = date.getMonth() + 1;
	var d = date.getDate();
	return y + '' + (m < 10 ? ('0' + m) : m) + '' + (d < 10 ? ('0' + d) : d);
}
function dateParser(s) {
	if (!s)
		return new Date();
	var y = s.substr(0, 4);
	var m = s.substr(4, 2);
	var d = s.substr(6, 2);
	if (!isNaN(y) && !isNaN(m) && !isNaN(d)) {
		return new Date(y, m - 1, d);
	} else {
		return new Date();
	}
}
</script>
</head>
<body class="easyui-layout" data-options="fit:true">
	<div data-options="region:'north',title:'查询条件'" style="height:75px;padding:5px;text-align:center">
		<table width="100%" class="editTable">
			<tr>
				<td class="tdTitle" width="10%">企业名称：</td>
				<td class="tdRight" width="20%"><input type="text" id="tenantNameSearch" name="tenantNameSearch" class="easyui-textbox"></td>
				<td class="tdTitle" width="10%">拼音码：</td>
				<td class="tdRight" width="20%"><input type="text" id="tenantSpellSearch" name="tenantSpellSearch" class="easyui-textbox"></td>
				<td class="tdTitle" width="10%">日期：</td>
				<td class="tdRight" width="20%"><input type="text" id="dateSearch" name="dateSearch" class="easyui-datebox" data-options="formatter:dateFormatter,parser:dateParser"></td>
				<td><a href="#" onclick="query()" class="easyui-linkbutton" iconCls="icon-search">查询</a></td>
			</tr>
		</table>
	</div>

	<div data-options="region:'center'">
		<table id="tenantList"></table>
	</div>

	<div id="editTenantWin" class="easyui-dialog" title="企业编辑" closed="true" style="width:280px;height:168px;">
		<form id="editTenantForm">
			<input type="hidden" id="_method" name="_method" value="put">
			<table width="100%" class="editTable">
				<tr>
					<td class="tdTitle" width="30%">企业编号：</td>
					<td class="tdRight"><input type="text" id="tenantId" name="tenantId" class="easyui-textbox" readonly /></td>
				</tr>
				<tr>
					<td class="tdTitle" width="30%">企业名称：</td>
					<td class="tdRight"><input type="text" id="tenantName" name="tenantName" class="easyui-textbox" /></td>
				</tr>
				<tr>
					<td class="tdTitle" width="30%">拼音码：</td>
					<td class="tdRight"><input type="text" id="tenantSpell" name="tenantSpell" class="easyui-textbox" /></td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>