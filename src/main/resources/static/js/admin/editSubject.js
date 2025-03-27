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
	window.openEditForm=function(subId) {
		$.ajax({
			url:"/admin/"+subId,
			method:"GET",
			success:function(response){
				console.log("Full API Response:", response);
				console.log("JSON Stringified Response:", JSON.stringify(response, null, 2));
				if (!response) {
				   console.error("No data received!");
				   return;
				}
				if (response.subId) {
					$("#editName").val(response.name);
					$("#editType").val(response.type);
					$("#editSemester").val(response.semester.semNo);
					$("#editSubId").val(response.subId);
					$("#editDepartment").val(response.department.deptName);
					if (response.semester) {
					            $("#editSemester").val(response.semester.semNo);
					        } else {
					            console.warn("Semester data is missing!");
					            $("#editSemester").val(""); // Clear field if no semester
					        }
				}else{
					console.error("Invalid subject data structure:", response);
				}
				loadAssignedProfessors(subId);
				$("#editFormContainer").css("right", "0");
			},
			error:function(){
				toastr.error("Failed to feach updated subject details");
			}
		});		
	};
	window.closeEditForm = function () {
	   $("#editFormContainer").css("right", "-100%"); // Slide out
	};
	window.deleteSubject=function(subId){
		Swal.fire({
		            title: "Are you sure?",
		            text: "Deleting this subject will also remove related data. This action cannot be undone.",
		            icon: "warning",
		            showCancelButton: true,
		            confirmButtonColor: "#d33",
		            cancelButtonColor: "#3085d6",
		            confirmButtonText: "Yes, delete it!"
		 }).then((result) => {
		            if (result.isConfirmed) {
						$.ajax({
							url:"/admin/subjects/delete/"+subId,
							xhrFields:{
								withCredentials: true
							},
							headers: { [window.csrfHeader]: window.csrfToken },
							method:"POST",
							success:function(response){
								$("#row-" + subId).remove();
								Swal.fire("Deleted!", "The subject has been deleted.", "success");
							
							},
							error:function(xhr){
								 Swal.fire("Failed to remove Subject");
							}
						});
					}
		});
	};
	function loadAssignedProfessors(subId){
		$.ajax({
			url:"/admin/subjects/"+subId+"/professors",
			type:"GET",
			success:function(professors){
				console.log(professors);	
				let container=$("#assignedProfessors");
				container.empty();
				if(!Array.isArray(professors) ||professors.length===0){
					container.append("<p>No Professors assinged.</p>");
					return;
				}
				professors.forEach(function(professor){
					if(typeof professor !=='object' || !professor.profId || !professor.name){
						console.warn("Skipping invalid professor data:", professor);
						return;
					}
					let tooltip=$("<div>")
						.addClass("professor-tooltip")
						.html(`<strong>${professor.name}</strong>(${professor.department})`)
						.hide();
					let button = $("<button>")
					    .text(professor.profId)
					    .addClass("professor-button")
					    .attr("data-prof-id", professor.profId)
					    .attr("data-prof-name", professor.name)
						.attr("data-prof-department",professor.department)
						.attr("type","button")
						.mouseenter(function () { // Show Tooltip on Hover
							$("body").append(tooltip); // Append to body to prevent container overflow
							let buttonOffset = $(this).offset();
							let buttonWidth = $(this).outerWidth();
							let buttonHeight = $(this).outerHeight();
							let tooltipWidth = tooltip.outerWidth();
							let tooltipHeight = tooltip.outerHeight();
							tooltip.css({
								top: buttonOffset.top - tooltipHeight - 10, // 10px gap above button
								left: buttonOffset.left + (buttonWidth / 2) - (tooltipWidth / 2) // Centered
							}).fadeIn(200);
						 })
						 .mouseleave(function () {  
						 	tooltip.fadeOut(200, function() { $(this).remove(); });
						 })
					     .dblclick(function() {
							let profId=$(this).attr("data-prof-id");
					         removeProfessor(subId, professor.profId, $(this));
							 tooltip.remove();
					     });
					container.append(button);
				});
			},
			error:function(){
				alert("Error fetching assigned professors");
			}
		});
	}
	function removeProfessor(subId,profId,buttonElement){
		$.ajax({
			url:"/admin/subjects/"+subId+"/removeProfessor/"+profId,
			xhrFields:{
						withCredentials: true
			},
			headers: { [window.csrfHeader]: window.csrfToken },
			method:"DELETE",
			success:function(response){
				toastr.success("Professor removed successfully")
				buttonElement.remove();
				let container=$("#assignedProfessors");
				if(container.children().length==0){
					container.append("<p>No professor assigned</p>");
				}
			},
			error:function(){
				
				toastr.error("Failed to remove Professor");
			}
		});
	}
	$("#saveSubjectBtn").click(function(){
		let updatedSubject={
			subId:$("#editSubId").val(),
			name:$("#editName").val(),
			department:$("#editDepartment").val(),
			type:$("#editType").val(),
			semNo:parseInt($("#editSemester").val().trim()),
			professorIds:[],
		};
		$(".professor-button").each(function(){
			updatedSubject.professorIds.push($(this).attr("data-prof-id"));
		});
		console.log("Saving subjects: "+updatedSubject);
		$.ajax({
			url:"/admin/update/subjects/"+updatedSubject.subId,
			method:"PUT",
			contentType:"application/json",
			data:JSON.stringify(updatedSubject),
			xhrFields:{
				withCredentials: true
			},
			headers: { [window.csrfHeader]: window.csrfToken },
			success:function(response){
				console.log("✅ Updated Subject:", updatedSubject);
				toastr.success("Subject updated successfully");
				let subjectRow = $("#row-" + updatedSubject.subId);
				if (subjectRow.length) {
				      subjectRow.find(".subject-name").text(updatedSubject.name);
				      subjectRow.find(".subject-type").text(updatedSubject.type);
				}
				//let subjectRow=$("#"+updatedSubject.subId);
				//subjectRow.find(".subject-name").text(updatedSubject.name);
				//subjectRow.find(".subject-type").text(updatedSubject.type);
				loadAssignedProfessors(updatedSubject.subId);
				$("#editFormContainer").css("right", "0");  // Ensure it stays open

				   console.log("Edit form updated successfully!");
			},
			error:function(xhr){
				toastr.error("Failed to updated subjects");
				console.log("Error:",xhr.responseText);
				
			}
		});
	});
		
});

