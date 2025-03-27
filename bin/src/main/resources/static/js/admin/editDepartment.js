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
	window.openEditForm=function(deptId) {
		$.ajax({
				url:"/admin/department/"+deptId,
				method:"GET",
				success:function(response){
					console.log("Full API Response:", response);
					            console.log("JSON Stringified Response:", JSON.stringify(response, null, 2));

					            if (!response) { 
					                console.error("Invalid department data structure:", response);
					                return;
					            }
								if(response.deptId){
									$("#editDeptId").val(response.deptId);    
										$("#editName").val(response.name);
										$("#editCode").val(response.code);
										$("#editPeriod").val(response.period);
										$("#editYear").val(response.year);
										let hodDropDown=$("#editHod");
										hodDropDown.empty();
										hodDropDown.append('<option value="">Select HOD</option>');
										response.professors.forEach(professor=>{
											let selected=professor.profId===response.hod?"selected":"";
											hodDropDown.append(
												`<option value="${professor.profId}"${selected}>${professor.name}(${professor.profId})</option>`);
												
										});
										$("#editFormContainer").css("right", "0");		
								}else{
									console.error("Invalid department data structure:", response);
								}
				            //$("#editFormContainer").css("right", "0")
				},
				error:function(){
					toastr.error("Failed to feach updated department details");
				}
			});		
		};
		$("#saveDepartmentBtn").click(function(){
			let updatedDepartment={
				deptId:$("#editDeptId").val(),
				name:$("#editName").val(),
				hod:$("#editHod").val(),
				code:$("#editCode").val(),
				period:parseInt($("#editPeriod").val().trim()),
				year:parseInt($("#editYear").val().trim())
			}
			console.log("Updated Departmets: "+updatedDepartment);
			$.ajax({
				url:"/admin/department/update/"+updatedDepartment.deptId,
				method:"PUT",
				contentType:"application/json",
				data:JSON.stringify(updatedDepartment),
				xhrFields:{
					withCredentials:true
				},
				headers: { [window.csrfHeader]: window.csrfToken },
				success:function(response){
					toastr.success("Department updated successfully");
					//$("#myTable").DataTable().ajax.reload();
					let row=$("#row-"+updatedDepartment.deptId);
					row.find(".department-deptName").text(updatedDepartment.name);
					row.find(".department-period").text(updatedDepartment.period);
					row.find(".department-year").text(updatedDepartment.year);
					let selectedHodName = $("#editHod option:selected").text().split("(")[0];
					row.find(".department-hod").text(selectedHodName !== "Select HOD" ? selectedHodName : "Not Assigned");
					//$("#editFormContainer").css("right", "-100%");
				},
				error:function(xhr){
					toastr.error(xhr.responseText);
				}
			});
		});
		window.deleteDepartment =function(deptId){
				Swal.fire({
				            title: "Are you sure?",
				            text: "Deleting this department will also remove related data. This action cannot be undone.",
				            icon: "warning",
				            showCancelButton: true,
				            confirmButtonColor: "#d33",
				            cancelButtonColor: "#3085d6",
				            confirmButtonText: "Yes, delete it!"
				 }).then((result) => {
				            if (result.isConfirmed) {
								$.ajax({
									url:"/admin/department/delete/"+deptId,
									xhrFields:{
										withCredentials: true
									},
									headers: { [window.csrfHeader]: window.csrfToken },
									method:"DELETE",
									success:function(response){
										$("#row-" + deptId).remove();
										Swal.fire("Deleted!", "The department has been deleted.", "success");
									
									},
									error:function(xhr){
										 Swal.fire("Failed to remove department");
									}
								});
							}
				});
			};
		window.closeEditForm = function () {
			   $("#editFormContainer").css("right", "-100%"); // Slide out
		};
});