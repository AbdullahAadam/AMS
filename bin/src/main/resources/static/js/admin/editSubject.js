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
	window.openEditForm=function(subId, name, department, type, semester) {
		$("#editSubId").val(subId);
		$("#editName").val(name);
		$("#editDepartment").val(department);
		$("#editType").val(type);
		$("#editSemester").val(semester);
		$("#editFormContainer").css("right","0");
		//let professorContainer=$("#assignedProfessors");
		//professorContainer.empty();
		loadAssignedProfessors(subId)
	};
	window.closeEditForm = function () {
	   $("#editFormContainer").css("right", "-100%"); // Slide out
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
			},
			error:function(){
				
				toastr.error("Failed to remove Professor");
			}
		});
	}
	$("#saveSubjectBtn").click(function(){
		
	});
	/*function showNotification(message, type) {
	       let notification = $("<div>")
	           .addClass("notification " + type)
	           .text(message);
	       $("body").append(notification);
	       setTimeout(function() {
	           notification.fadeOut(500, function() { $(this).remove(); });
	       }, 3000); // Auto-hide after 3 seconds
	   }*/		        
	

	
	
	
		
});

