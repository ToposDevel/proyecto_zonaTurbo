jQuery(document).ready(
		function() {
			var _onClickExcelExport = function(e) {
				var table = $("table.dataTable").parent().html();
				var title = $(".caption-subject").text().replace(' ', '_');
				var data_type = 'data:application/vnd.ms-excel';

				var a = document.createElement('a');
				a.href = data_type + ', ' + table.replace(/ /g, '%20');
				a.download = title + '.xls';

				a.click();

				e.preventDefault();
			};

			var _onClickCancelButton = function() {
				window.history.back();
			};

			var _onClickEditButton = function(e) {
				e.preventDefault();

				var action = $("#javaForm").attr("action");
				var path = $(this).attr("r-path");
				var fullPath = $(this).attr("full-path");
				var id = $(this).parent().parent().find("td:eq(1)").text();

				if (path == "/delete"
						&& confirm("Do you really want delete this registry?"))
					location = (fullPath == undefined ? action : fullPath)
							+ path + "/" + id;

				if (path == "/newStatus")
					location = (fullPath == undefined ? action : fullPath)
							+ path + "/" + id + "/"
							+ $(this).attr("new-status");

				if (path != "/delete")
					location = (fullPath == undefined ? action : fullPath)
							+ path + "/" + id;
				
			};
			
			$('.btn-status').on('click', _onClickEditButton);
			$('.btn-edit').on('click', _onClickEditButton);
			$('.btn-delete').on('click', _onClickEditButton);

			$("button[type=button].default:contains('Regresar')").click(
					_onClickCancelButton);
			$("button[type=button].default:contains('Registrar')").click(
					_onClickCancelButton);

			$(".dropdown-menu a:contains('Export to Excel')").click(
					_onClickExcelExport);
		});