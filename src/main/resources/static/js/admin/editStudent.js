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
	window.openEditForm=function(regNo){			
			$.ajax({
				url:"/admin/student/edit/"+regNo,
				method:"GET",
				success:function(response){
					console.log("datas"+response);
					if(!response || !response.regNo){
						toastr.error("Failed to load student details.");
						return;
					}
					$("#editRegNo").val(response.regNo);
					$("#editName").val(response.name);
					$("#editEmail").val(response.email);
					$("#editDepartment").val(response.deptName);
					$("#editDepartment").attr('data-dept-id',response.deptId);
					var deptId = $("#editDepartment").attr('data-dept-id');
					let professorSelect = $("#editProfessor");
					professorSelect.empty().append('<option value="">Select Professor</option>');
					//$("#editProfessor").empty().append('<option value="">Select Professor</option>');
					if (deptId) {
					   $.ajax({
					      url: "/admin/professors/" + deptId,
					      type: "GET",
					      success: function (data) {
							//let professorSelect=$("#editProfessor");
							//professorSelect.empty();
					          if (data.length === 0) {
					              $("#professorError").text("No professors available for this department.").show();
					          } else {
					              $.each(data, function (index, professor) {
					              	$("#editProfessor").append('<option value="' + professor.profId + '">' + professor.name + '</option>');
					              });
								  if (!response.profId || !data.some(p => p.profId === response.profId)) {
								    $("#professorError").text("The mentor professor has been deleted. Please assign a new one.").show();
								  } else {
								    professorSelect.val(response.profId); 
								    $("#professorError").hide();
								 }
					           }
					     },
					     error: function (xhr, status, error) {
					           console.log("Error fetching Student Details:", error);
					     }
					  });
					}
					$("#editFormContainer").css("right", "0");
				},
				error:function(){
					toastr.error("Failed to fetch professor details");
				}
				
			});
	}
	window.deleteStudent=function(regNo){
				Swal.fire({
				            title: "Are you sure?",
				            text: "Deleting this student will  remove permenatly. This action cannot be undone.",
				            icon: "warning",
				            showCancelButton: true,
				            confirmButtonColor: "#d33",
				            cancelButtonColor: "#3085d6",
				            confirmButtonText: "Yes, delete it!"
				 }).then((result) => {
				            if (result.isConfirmed) {
								$.ajax({
									url:"/admin/student/delete/"+regNo,
									xhrFields:{
										withCredentials: true
									},
									headers: { [window.csrfHeader]: window.csrfToken },
									method:"DELETE",
									success:function(response){
										$("#row-" + regNo).remove();
										Swal.fire("Deleted!", "The student has been deleted.", "success");
									
									},
									error:function(xhr){
										 Swal.fire("Failed to remove student");
									}
								});
							}
				});
			};
	
	
	
	
	
	
	$("#saveSubjectBtn").click(function () {
		const professor = $("#editProfessor").val();
		const professorName=$("#editProfessor option:selected").text();        
		if (!professor) {
		            if ($("#editProfessor option").length === 0) {
		                $("#professorError").text("No professors available for this department.").show();
		            } else {
		                $("#professorError").text("Professor is required.").show();
		            }
		            $("#editProfessor").addClass("error-border");
		            return;
		        }
		let studRegNo=$("#editRegNo").val();
		let updatedStudent={
			name:$("#editName").val(),
			email:$("#editEmail").val(),
			profId:$("#editProfessor").val(),
			
		};
		console.log("Updated Student: "+updatedStudent);
		$.ajax({
			url:"/admin/student/update/"+studRegNo,
			method:"PUT",
			contentType:"application/json",
			data:JSON.stringify(updatedStudent),
			xhrFields:{
				withCredentials: true
			},
			headers: { [window.csrfHeader]: window.csrfToken },
			success:function(response){
				toastr.success("Student updated successfully");
				let row = $('#row-' + studRegNo);
				row.find('.student-name').text(updatedStudent.name);
				row.find('.student-email').text(updatedStudent.email);				
				row.find('.student-mentor').text(professorName); 
				$("#editFormContainer").css("right", "0"); 
						//$("#editFormContainer").hide();
			},
				error: function () {
				toastr.error("Failed to update professor details.");
			}
		});
				
	});
	window.closeEditForm = function () {
			$("#editFormContainer").css("right", "-100%"); // Slide out
	};
	$("#editProfessor").change(function () {
	        if ($(this).val()) {
	            $("#professorError").hide();
	            $(this).removeClass("error-border");
	        }
	});

	
});