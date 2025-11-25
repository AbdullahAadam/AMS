$(document).ready(function(){
	$.get("/csrf", function(data) {
				// data might look like: { parameterName: "_csrf", token: "abc123", headerName: "X-CSRF-TOKEN" }
				window.csrfToken = data.token;
				window.csrfHeader = data.headerName;
				console.log("CSRF Token retrieved:", window.csrfToken);
		});
		$(".menu-item").click(function(){
						var url=$(this).attr('data-href');
						window.location.href=url;
					});
				$(".submenu").on("click", function() {
				        let submenuIcon = $(this).find(".images");
				        $(".submenu").not(this).removeClass("active").find(".images").attr("src", "/images/admin/arrow2.png");
				        $(this).toggleClass("active");
				        submenuIcon.attr("src", $(this).hasClass("active") ? "/images/admin/arrow1.png" : "/images/admin/arrow2.png");
				  });
	$("#myTable").DataTable();
	
	window.openEditForm=function(regNo){
			//alert("hello")
			$.ajax({
				url:"/professor/student/edit/"+regNo,
				method:"GET",
				success:function(response){
					if(!response || !response.regNo){
						toastr.error("Failed to load Student details.");
						return;
					}
					$("#editRegNo").val(response.regNo);
					$("#editName").val(response.name);
					$("#editDepartment").val(response.dept);
					$("#editBatch").val(response.batch);
					$("#editYear").val(response.year);
					$("#editGender").val(response.gender);
					$("#editDate").val(response.joinDate);
					$("#editStatus").val(response.status);
					$("#editFormContainer").css("right", "0");
				},
				error:function(){
					toastr.error("Failed to fetch Student details");
				}
				
			});
		}
		$("#saveSubjectBtn").click(function () {
			    let regNo = $("#editRegNo").val();
			    let updatedStudent = {
			        name: $("#editName").val(),
					gender:$("#editGender").val(),
			        batch: $("#editBatch").val(),			 
					joinDate: $("#editDate").val(),
					status:$("#editStatus").val(),
					year:$("#editYear").val(),
			    };
				console.log("Updating Student:", regNo, updatedStudent);
				$.ajax({
					url:"/professor/student/update/"+regNo,
					method:"PUT",
					contentType:"application/json",
					data:JSON.stringify(updatedStudent),
					xhrFields:{
						withCredentials: true
					},
					headers: { [window.csrfHeader]: window.csrfToken },
					success:function(response){
						toastr.success("Student updated successfully");
						let row = $('#student-' + regNo);
						row.find('.student-name').text(updatedStudent.name);
						row.find('.student-batch').text(updatedStudent.batch);
						row.find('.student-joinDate').text(updatedStudent.joinDate);
						row.find('.student-status').text(updatedStudent.status); 
						row.find('.student-year').text(updatedStudent.year);
						$("#editFormContainer").css("right", "0"); 
						//$("#editFormContainer").hide();
					},
					error: function (xhr) {
						if (xhr.status === 400) {
						     toastr.error(xhr.responseText); 
						} else {
						     toastr.error("Failed to update Student details.");
						}
					}
				});
				
			});
			$("#editDate").change(function () {
			    let newJoinDate = $(this).val();
			    let regNo = $("#editRegNo").val();

			    if (newJoinDate) {
			        $.ajax({
			            url: "/professor/student/updateYear",
			            type: "GET",
			            data: { regNo: regNo, newJoinDate: newJoinDate },
			            success: function (response) {
			                console.log("Updated Year:", response.year); // Debugging
			                $("#editYear").val(response.year); // Correctly assign the Roman numeral
			            },
			            error: function (xhr) {
			                toastr.error("Failed to calculate the year.");
			            }
			        });
			    }
			});

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
											url:"/professor/student/delete/"+regNo,
											xhrFields:{
												withCredentials: true
											},
											headers: { [window.csrfHeader]: window.csrfToken },
											method:"DELETE",
											success:function(response){
												$("#student-" + regNo).remove();
												Swal.fire("Deleted!", "The student has been deleted.", "success");
											
											},
											error:function(xhr){
												 Swal.fire("Failed to remove student");
											}
										});
									}
						});
					};
		window.closeEditForm = function () {
				   $("#editFormContainer").css("right", "-100%"); // Slide out
		};
		
});