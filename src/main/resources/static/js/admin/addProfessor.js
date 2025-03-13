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
	$.ajax({
	    url:'/admin/listDepartments',
	    type: 'GET',
	    dataType: 'json',
	    success: function(data) {
			console.log(data);
		    $.each(data, function(index, department) {
		       $('#department').append(
		       	$('<option>', {
					value: department.deptId,
					text: department.deptName
		         })
		       );
		    });
	    },
	    error: function(xhr, status, error) {
	        console.log("Error fetching departments:", error);
	    }
	 });
	//alter('professor');
	//console.log("professor.js");
	//console.log(showToastr);
	//console.log(toastrType);
	//console.log(toastrMessage);
	
		//var toastrType='[[${toastrType}]]';
		//var toastrMessage='[[${toastrMessage}]]';
		/*switch(toastrType){
			case "success":
				toastr.success(toastrMessage);
				break;
			case "error":
				toastr.error(toastrMessage);
				break;
			case "info":
				toastr.info(toastrMessage);
				break;
			case "warning":
				toastr.warning(toastrMessage);
				break;
			default:
				console.log("invalid Toastr");
		}*/
		$(".menu-item a").filter(function() {
		       return $(this).text().trim() === "Add Professor";  // Matching exact text
		   }).on("click", function() {
		       toastr.warning("The Professor ID is created automatically.");
		   });
            function validateForm(){
                let isValid=true;
                let InvalidField=null;
				var regex = /^[A-Za-z\s]+$/;
                $(".error-message").hide();
                $("input,select").removeClass("error-border");
                const professorName=$("#professorName").val().trim();
                if(!professorName){
                    $("#professorNameError").text("Professor Name is required.").show();
                    $("#professorName").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#professorName");
                    isValid=false;
                }else if(professorName.length <3 || professorName.length>50){
                    $("#professorNameError").text("Professor Name must between 3 to 50 characters").show();
                    $("#professorName").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#professorName");
                    isValid=false;
                }
				if(!regex.test(professorName)){
					$("#professorNameError").text("Invalid Name format.").show();
					$("#professorName").addClass("error-border");
					if(!InvalidField)InvalidField=$("#professorName");
					isValid=false;
				}
                const email=$("#email").val().trim();
                const emailPattern=/^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if(!email){
                    $("#professorEmailError").text("Email must required").show();
                    $("#email").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#email");
                    isValid=false;
                }else if(!emailPattern.test(email)){
                    $("#professorEmailError").text("Invalid Email Format").show();
                    $("#email").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#email");
                    isValid=false;
                }
				const department=$("#department").val();
				if(!department){
				   $("#departmentError").text("Department must required").show();
				   $("#department").addClass("error-border");
				   if(!InvalidField)InvalidField=$("#department");
				   isValid=false;
				}
                const professorRole=$("#professorRole").val();
                if(!professorRole){
                    $("#professorRoleError").text("Role must required").show();
                    $("#professorRole").addClass("error-border");
                    if(!InvalidField)InvalidField=$("#professorRole");
                    isValid=false;
                }
               
                $("#professorName,#email").keydown(function(){
                    $(".error-message").hide();
                    $("input").removeClass("error-border");
                });
				$("#department").click(function(){
				    $(".error-message").hide();
				    $("select").removeClass("error-border");
				});
				if(InvalidField){
					InvalidField.focus();
				}
                return isValid;
            }
            function creatingId(){
                var name=$("#professorName").val().trim();
                //var role=$("#professorRole").val();
                var totalLetters=name.replace(/\s+/g,'').length;
                const num=Math.floor(Math.random()*100);
                var nameParts=name.split(" ");
                // if(!name){
                //     $("#professorName").val('');
                //     return;
                // }
                var initial='';
                for(var i=0;i<nameParts.length;i++){
                    initial=initial+nameParts[i].charAt(0).toUpperCase();
                }
                var professorId="P-"+initial+totalLetters+"-"+num;
                $("#professorId").val(professorId);
            }
            $("#professorName	").on('input change',function(){
                creatingId();
            });
            $("#addProfessorForm").submit(function(event){
                event.preventDefault();
                if(!validateForm()){
                    return;
                }
				
				professorData={
					name:$("#professorName").val(),
					email:$("#email").val().trim(),
					password:$("#email").val().trim().split("@")[0],
					deptId:$("#department").val(),
					role:$("#professorRole").val(),
					profId:$("#professorId").val(),										
				};
				console.log("data");
				console.log(professorData);
				$.ajax({
					url:"/admin/addProfessor",
					method:"POST",
					contentType:"application/json",
					xhrFields:{
						withCredentials: true
					},
					headers: { [window.csrfHeader]: window.csrfToken },
					data:JSON.stringify(professorData),
					success:function(response){
						console.log("Success: ",response);
						toastr.success(response);
						$("#addProfessorForm").trigger("reset");
					},
					error:function(xhr,status,error){
						var errorMessage = xhr.responseText;

						        // Check for specific error messages and handle them
						        if (errorMessage.startsWith("Error:")) {
						            errorMessage = errorMessage.substring("Error:".length).trim(); // Remove "Error:" prefix

						            // Handle specific error cases
						            if (errorMessage.includes("Department ID must required")) {
						                toastr.warning("Department ID is required.");
						            } else if (errorMessage.includes("Department not found")) {
						                toastr.warning("The specified department does not exist.");
						            } else if (errorMessage.includes("already has an HOD assigned")) {
						                toastr.warning(errorMessage); // Display the HOD assignment error
						            } else if (errorMessage.includes("Professor already exists")) {
						                toastr.warning("A professor with the same ID already exists.");
						            } else if (errorMessage.includes("Email already exists")) {
						                toastr.warning("A professor with the same email already exists.");
						            } else {
						                // Handle other generic errors
						                toastr.error(errorMessage || "An unexpected error occurred.");
						            }
						        } else {
						            // Handle non-prefixed errors or unexpected errors
						            toastr.error(errorMessage || "An unexpected error occurred.");
						        }

						        // Log the error for debugging
						        console.log('Error: ' + status + ' - ' + error);
						        console.log('Response: ' + xhr.responseText);
					}
				});		
            });
        });
        