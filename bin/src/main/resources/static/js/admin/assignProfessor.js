/**
 * 
 */
$(document).ready(function () { 
    // Fetch Departments
    $.ajax({
        url: '/admin/listDepartments',
        type: 'GET',
        dataType: 'json',
        success: function (data) {
			console.log(data);
            if (data.length === 0) {
                $("#departmentError,#departmentSubjectError").text("No departments available.").show();
            } else {
                $.each(data, function (index, department) {
                    $('#department,#departmentSubject').append(
                        $('<option>', {
                            value: department.deptId,
                            text: department.deptName,
                            id: department.code
                        })
                    );
                });
            }
        },
        error: function (xhr, status, error) {
            console.log("Error fetching departments:", error);
        }
    });

    // Fetch Professors based on Department Selection
    $("#department").change(function () {
        var deptId = $(this).val();
        $("#professor").empty().append('<option value="">Select Professor</option>');

        if (deptId) {
            $.ajax({
                url: "/admin/professors/" + deptId,
                type: "GET",
                success: function (data) {
					console.log(data);
                    if (data.length === 0) {
                        $("#professorError").text("No professors available for this department.").show();
                    } else {
                        $.each(data, function (index, professor) {
                            $("#professor").append('<option value="' + professor.profId + '">' + professor.name + '</option>');
                        });

                        // Auto-select the only professor if only one is available
                        if (data.length === 1) {
                            $("#professor").val(data[0].profId);
                        }

                        $("#professorError").hide();
                    }
                },
                error: function (xhr, status, error) {
                    console.log("Error fetching professors:", error);
                }
            });
        }
    });
	//Fetching based on Department for Subject
	$("#departmentSubject").change(function () {
	        var deptId = $(this).val();
	        $("#subject").empty().append('<option value="">Select Subject</option>');

	        if (deptId) {
	            $.ajax({
	                url: "/admin/subjects/" + deptId,
	                type: "GET",
	                success: function (data) {
						console.log(data);
	                    if (data.length === 0) {
	                        $("#subjectError").text("No subjects available for this department.").show();
	                    } else {
	                        $.each(data, function (index, subject) {
	                            $("#subject").append('<option value="' + subject.subId + '">' + subject.name +'(Sem-'+subject.semester.semNo +')' +'</option>');
	                        });

	                        // Auto-select the only professor if only one is available
	                        if (data.length === 1) {
	                            $("#subject").val(data[0].profId);
	                        }

	                        $("#departmentSubjectError").hide();
	                    }
	                },
	                error: function (xhr, status, error) {
	                    console.log("Error fetching subjects:", error);
	                }
	            });
	        }
	    });
	
	
    
	function validateForm() {
	    let isValid = true;
	    let invalidField = null;
	    $(".error-message").hide();
	    $("input, select").removeClass("error-border");

	    // Validate Department
	    const department = $("#department").val();
	    if (!department) {
	        $("#departmentError").text("Department is required.").show();
	        $("#department").addClass("error-border");
	        isValid = false;
	    }
	    const professor = $("#professor").val();
	    if (!professor) {
	        if ($("#professor option").length > 1) {
	            $("#professorError").text("Professor is required.").show();
	            $("#professor").addClass("error-border");
	            isValid = false;
	        } else {
	            $("#professorError").text("No professors available for this department.").show();
	            $("#professor").addClass("error-border");
	            isValid = false;
	        }
	    }
		const departmentSubject = $("#departmentSubject").val();
		if (!departmentSubject) {
			$("#departmentSubjectError").text("Department is required for subject.").show();
			$("#departmentSubject").addClass("error-border");
			isValid = false;
		}
		const subId = $("#subject").val();
		    if (!subId) {
				if ($("#subject option").length > 1) {
				       $("#subjectError").text("Subject is required.").show();
				}else {
				       $("#subjectError").text("No subjects available for this department.").show();
				}
				$("#subject").addClass("error-border");
				isValid = false;
			}
	    return isValid;	
	}
    $("#professor").change(function () {
        if ($(this).val()) {
            $("#professorError").hide();
            $(this).removeClass("error-border");
        }
    });

    $("#assignProfessorForm").submit(function (event) {
        event.preventDefault();
        if (!validateForm()) {
            return;
        }

        let subjectData = {
            //deptId:$("#department").val(),
			profId:$("#professor").val(),
			//deptSubId:$("#departmentSubject").val(),
			subId:$("#subject").val(),
        };
		console.log("datas: ",subjectData);
		$.ajax({
			url:"/admin/assignProfessor",
			method:"POST",
			contentType:"application/json",
			xhrFields:{
				withCredentials:true
			},
			headers:{[window.csrfHeader]: window.csrfToken},
			data:JSON.stringify(subjectData),
			success:function(response){
				console.log("Success: "+response);
				toastr.success(response);
				$("#assignProfessorForm").trigger("reset");
			},
			error:function(xhr,status,error){
				if(xhr.responseText.indexOf("already exists")!==-1){					
					var errorMessage=xhr.responseText;
					if(errorMessage.startsWith("Error:")){
						errorMessage=errorMessage.substring("Error:".length).trim();
					}
					toastr.warning(errorMessage);
					console.log(errorMessage);
					}else{
						toastr.error(xhr.responseText||"An unexpected error occur");
						console.log('Error: ' + status + ' - ' + error);
						console.log('Response: ' + xhr.responseText);
					}
			}
		});

       
    });
	$("#department,#departmentSubject,#subject").click(function(){
		$(".error-message").hide();
		$("select").removeClass("error-border");
	});
});