/**
 * 
 */
$(document).ready(function(){
	$.get("/csrf", function(data) {
			// data might look like: { parameterName: "_csrf", token: "abc123", headerName: "X-CSRF-TOKEN" }
			window.csrfToken = data.token;
			window.csrfHeader = data.headerName;
			console.log("CSRF Token retrieved:", window.csrfToken);
	});
	$("#myTable").DataTable();
	window.openEditForm=function(profId){
		//alert("hello")
		$.ajax({
			url:"/admin/professor/edit/"+profId,
			method:"GET",
			success:function(response){
				if(!response || !response.profId){
					toastr.error("Failed to load professor details.");
					return;
				}
				$("#editProfId").val(response.profId);
				$("#editName").val(response.name);
				$("#editEmail").val(response.email);
				$("#editRole").val(response.role);
				$.ajax({
					url:"/admin/listDepartments",
					type:"GET",
					dataType:'json',
					success:function(departments){
						let departmentSelect=$("#editDepartment");
						departmentSelect.empty();
						
						$.each(departments,function(index,department){
							let option=$("<option>",{
								value:department.deptId,
								text:department.deptName
							});
							departmentSelect.append(option);
						});
						departmentSelect.val(response.department.deptId);
					},
					error:function(){
						toastr.error("Failed to load Departments");
					}
					
				});
				$("#editFormContainer").css("right", "0");
			},
			error:function(){
				toastr.error("Failed to fetch professor details");
			}
			
		});
	}
	window.deleteProfessor=function(profId){
			Swal.fire({
			            title: "Are you sure?",
			            text: "Deleting this professor will  remove permenatly. This action cannot be undone.",
			            icon: "warning",
			            showCancelButton: true,
			            confirmButtonColor: "#d33",
			            cancelButtonColor: "#3085d6",
			            confirmButtonText: "Yes, delete it!"
			 }).then((result) => {
			            if (result.isConfirmed) {
							$.ajax({
								url:"/admin/professor/delete/"+profId,
								xhrFields:{
									withCredentials: true
								},
								headers: { [window.csrfHeader]: window.csrfToken },
								method:"DELETE",
								success:function(response){
									$("#row-" + profId).remove();
									Swal.fire("Deleted!", "The professor has been deleted.", "success");
								
								},
								error:function(xhr){
									 Swal.fire("Failed to remove professor");
								}
							});
						}
			});
		};
	
	$("#saveSubjectBtn").click(function () {
	    let profId = $("#editProfId").val();
	    let updatedProfessor = {
	        name: $("#editName").val(),
	        email: $("#editEmail").val(),
	        role: $("#editRole").val(),
	        //department:{
			deptId:$("#editDepartment").val()
			//}  // Get selected department ID
	    };
		console.log("Updating Professor:", profId, updatedProfessor);
		$.ajax({
			url:"/admin/professor/update/"+profId,
			method:"PUT",
			contentType:"application/json",
			data:JSON.stringify(updatedProfessor),
			xhrFields:{
				withCredentials: true
			},
			headers: { [window.csrfHeader]: window.csrfToken },
			success:function(response){
				toastr.success("Professor updated successfully");
				let row = $('#row-' + profId);
				row.find('.professor-name').text(updatedProfessor.name);
				row.find('.professor-email').text(updatedProfessor.email);
				row.find('.professor-role').text(updatedProfessor.role);
				row.find('.professor-department').text($("#editDepartment option:selected").text()); 
				$("#editFormContainer").css("right", "0"); 
				//$("#editFormContainer").hide();
			},
			error: function (xhr) {
				if (xhr.status === 400) {
				     toastr.error(xhr.responseText); 
				} else {
				     toastr.error("Failed to update professor details.");
				}
			}
		});
		
	});
	window.closeEditForm = function () {
		   $("#editFormContainer").css("right", "-100%"); // Slide out
	};
});